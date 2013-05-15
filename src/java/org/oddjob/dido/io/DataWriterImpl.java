package org.oddjob.dido.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataPlan;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Selectable;
import org.oddjob.dido.SupportsChildren;

/**
 * Write data out.
 * 
 * @author rob
 *
 * @param <ACCEPTS_OUT> The type of {@link DataOut} this writer accepts.
 */
public class DataWriterImpl<ACCEPTS_OUT extends DataOut> extends AbstractNodeWalker
implements LinkableOut, DataWriter {
	private static final Logger logger = Logger.getLogger(DataWriterImpl.class);
	
	private final Map<DataNode<?, ?, ?, ?>, DataLinkOut> links =
		new HashMap<DataNode<?, ?, ?, ?>, DataLinkOut>();
	
	private final Stack<WhatNowOut> whats = 
		new Stack<WhatNowOut>();	
	
	private final ArooaSession session;
	
	private final ConfigurationStrategy configurationStrategy;
	
	public DataWriterImpl(DataNode<?, ?, ACCEPTS_OUT, ?> root,
			ACCEPTS_OUT dataOut) {
		this.whats.push(new WhatNowOut(root, dataOut));
		this.configurationStrategy = ConfigurationType.NEVER;
		this.session = null;
	}
	
	public DataWriterImpl(DataPlan<?, ?, ACCEPTS_OUT, ?> root, 
			ACCEPTS_OUT dataOut) {
		this(root, dataOut, ConfigurationType.INITIAL);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param origin
	 * @param dataOut
	 */
	public DataWriterImpl(DataPlan<?, ?, ACCEPTS_OUT, ?> origin, 
			ACCEPTS_OUT dataOut,
			ConfigurationStrategy configurationStrategy) {
		this.whats.push(new WhatNowOut(origin.getTopNode(), dataOut));
		this.session = origin.getSession();
		this.configurationStrategy = configurationStrategy == null ? 
				ConfigurationType.INITIAL : configurationStrategy;
		
		this.configurationStrategy.configureInitially(session, origin.getTopNode());
	}
		
	/**
	 * Write a bean out.
	 * 
	 * @param bean An object (normally a bean, but could be a Map) that
	 * provides the data to be written.
	 * 
	 * @throws DataException
	 */
	public boolean write(Object bean) throws DataException {
		
		if (bean == null) {
			logger.debug("Completing structure at depth " + whats.size());
		}
		else {
			logger.debug("Writing bean into structure at depth " + whats.size());
		}
		
		while (!whats.isEmpty()) {
			
			final WhatNowOut now = whats.peek();
	
			final DataNode<?, ?, ?, ?> current = now.getCurrent();
			
			switch(now.getState()) {
			case NEW:

				configurationStrategy.configureEvery(session, current);
				
				if (now.advance(notifyDataOut(current, bean))) {
					notifyListenersStart(current);
				}
				else {
					whats.pop();
				}
								
				break;
			case CHILDREN:
				
				WhatNowOut childNext = now.nextChild();
				
				if (childNext != null) {
					whats.push(childNext);
				}
				
				break;
			case PROCESSED:
				
				notifyListenersEnd(current);
				
				now.setRunIfTheEnd(new RunnableLike() {
					@Override
					public void run() throws DataException {
						notifyLastOut(current);
					}
				});
				
				if (now.after()) {					
					if (bean != null) {
						return true;
					}
				}
				else {
					whats.pop();										
				}
				break;
			}
			
			if (whats.isEmpty()){
				now.writerComplete();
			}
		}		
		
		return false;
	}

	public void complete() throws DataException {
		write(null);
	}
	
	protected WhatNowOut.Navigation notifyDataOut(DataNode<?, ?, ?, ?> node, Object bean) {
		
		if (bean == null) {
			return WhatNowOut.Navigation.COMPLETE;
		}

		DataLinkOut link = this.links.get(node);
		if (link != null) { 
			boolean selected = link.dataOut(
					new LinkOutEvent(this, node), bean);

			if (node instanceof Selectable) {
				((Selectable) node).setSelected(selected);
			}
			
			if (selected) {
				return WhatNowOut.Navigation.VISIT;
			}
			else {
				return  WhatNowOut.Navigation.IGNORE;
			}
		}
		else {
			
			if (node instanceof SupportsChildren) {
				
				DataNode<?, ?, ?, ?>[] children = 
					((SupportsChildren) node).childrenToArray(); 
					
				for (DataNode<?, ?, ?, ?> child : children) {

					notifyDataOut(child, bean);				
				}
			}
						
			return WhatNowOut.Navigation.VISIT;
		}
	}
	
	protected void notifyLastOut(DataNode<?, ?, ?, ?> node) {
		
		DataLinkOut link = this.links.get(node);
		if (link != null) {
			link.lastOut(new LinkOutEvent(this, node));
		}
	}
	
	@Override
	public void setLinkOut(DataNode<?, ?, ?, ?> node, DataLinkOut controler) {
		if (node == null) {
			throw new NullPointerException("Node.");
		}
		if (controler == null) {
			links.remove(controler);
		}
		else {
			this.links.put(node, controler);
		}
	}	
}
