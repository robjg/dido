package org.oddjob.dido.io;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataPlan;

/**
 * Read data in.
 * 
 * @author rob
 *
 * @param <ACCEPTS_IN> The type of {@link DataIn} this reader accepts.
 */
public class DataReader<ACCEPTS_IN extends DataIn> 
extends AbstractNodeWalker implements LinkableIn {
	private static final Logger logger = Logger.getLogger(DataWriter.class);

	private final Map<DataNode<?, ?, ?, ?>, DataLinkIn> links =
		new HashMap<DataNode<?, ?, ?, ?>, DataLinkIn>();
	
	private final Stack<WhatNowIn> whats = 
		new Stack<WhatNowIn>();	

	private final ArooaSession session;
	
	private final ConfigurationStrategy configurationStrategy;
	
	public DataReader(DataNode<ACCEPTS_IN, ?, ?, ?> root,
			ACCEPTS_IN dataIn) {
		this.whats.push(new NewWhatNowIn(root, dataIn));
		this.configurationStrategy = ConfigurationType.NEVER;
		this.session = null;
	}
	
	public DataReader(DataPlan<ACCEPTS_IN, ?, ?, ?> origin, 
			ACCEPTS_IN dataIn) {
		this(origin, dataIn, ConfigurationType.INITIAL);
	}
	
	public DataReader(DataPlan<ACCEPTS_IN, ?, ?, ?> origin, 
			ACCEPTS_IN dataIn, ConfigurationStrategy configurationStrategy) {
		this.whats.push(new NewWhatNowIn(origin.getTopNode(), dataIn));
		this.session = origin.getSession();
		this.configurationStrategy = configurationStrategy;
		
		configurationStrategy.configureInitially(session, origin.getTopNode());
	}
	
	public Object read() throws DataException {
		
		logger.debug("Reading bean from structure at depth " + whats.size());
		
		while (!whats.isEmpty()) {
			
			WhatNowIn now = whats.pop();
	
			final DataNode<?, ?, ?, ?> current = now.getCurrent();
			
			switch(now.getState()) {
			case NEW:
				
				configurationStrategy.configureEvery(session, current);

				WhatNowIn next = now.currentIn();
				
				if (next != null) {
					whats.push(next);
					next.setControl(notifyDataIn(current));
					notifyListenersStart(current);
				}
				
				break;
			case CHILDREN:
				
				Collection<WhatNowIn> childNext = now.nextChild();
				whats.addAll(childNext);
				
				break;
			case PROCESSED:

				notifyListenersEnd(current);
				
				now.setRunIfTheEnd(new RunnableLike() {
					@Override
					public void run() throws DataException {
						notifiyLastIn(current);
					}
				});
				
				
				WhatNowIn out = now.getAfter();
				
				if (out != null) {
					whats.push(out);					
				}
				
				LinkInControl control = now.getControl();
				
				if (control != null) {
					Object data = control.getDataObject();
					if (data != null) {
						return data;
					}
				}
				
				break;
			}			
		}
		return null;
	}

	protected LinkInControl notifyDataIn(DataNode<?, ?, ?, ?> node) {
		
		LinkInEvent event = new LinkInEvent(this, node);
		
		DataLinkIn link = this.links.get(node);
		if (link != null) {
			return link.dataIn(event);
		}
		return null;
	}
	
	protected void notifiyLastIn(DataNode<?, ?, ?, ?> node) {
		
		LinkInEvent event = new LinkInEvent(this, node);
		
		DataLinkIn listeners = this.links.get(node);
		if (listeners != null) {
			listeners.lastIn(event);
		}
	}
	
	@Override
	public void setControlIn(DataNode<?, ?, ?, ?> node, DataLinkIn controler) {
		if (node == null) {
			throw new NullPointerException("Node.");
		}
		if (controler == null) {
			this.links.remove(node);
		}
		else {
			this.links.put(node, controler);
		}
		
	}	
}
