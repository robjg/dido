package org.oddjob.dido.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.oddjob.dido.DataDriver;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.WhereNext;

/**
 * Internal class for tracking node processing.
 * 
 * @author rob
 *
 */

public class WhatNowOut {

	enum State{
		NEW,
		CHILDREN,
		PROCESSED
	}
	
	enum Navigation {
		VISIT,
		COMPLETE,
		IGNORE,
	}
	
	private interface Behaviour {
		
		abstract public boolean advance(Navigation navigation) 
		throws DataException;
		
		abstract public WhatNowOut nextChild() throws DataException;

		abstract public State getState();
		
		abstract public boolean after() throws DataException;
	}
	
	private DataNode<?, ?,?, ?> current;
	
	private DataOut data; 
	
	protected RunnableLike runIfTheEnd;
	
	private List<WhatNowOut> children = new ArrayList<WhatNowOut>();
	
	private Behaviour behaviour;
	
	protected WhatNowOut(WhatNowOut existing) {
		this(existing.current, existing.data);
	}
	
	public WhatNowOut(DataNode<?, ?, ?, ?> current, DataOut data) {
		if (current == null) {
			throw new NullPointerException("No Current Node.");
		}
		if (data == null) {
			throw new NullPointerException("No Current Data.");
		}
		this.current = current;
		this.data = data;
		this.behaviour = new NewWhatNowOut();
	}
	
	private void modifyBehaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
	}
	
	public boolean advance(Navigation navigation) 
	throws DataException {
		return behaviour.advance(navigation);
	}
	
	public WhatNowOut nextChild() throws DataException {
		return behaviour.nextChild();
	}

	public DataNode<?, ?, ?, ?> getCurrent() {
		return current;
	}

	protected DataOut getData() {
		return data;
	}
	
	public State getState() {
		return behaviour.getState();
	}
	
	public boolean after() throws DataException {
		return behaviour.after();
	}
	
	public void writerComplete() throws DataException {
		if (! (current instanceof DataDriver)) {
			fireComplete();
		}
	}
		
	protected void theEndOfThisType() throws DataException {
		if (runIfTheEnd != null) {
			runIfTheEnd.run();
		}
	}

	protected void fireComplete() throws DataException {
			
		for (WhatNowOut child : children) {
			if (child.getCurrent() instanceof DataDriver) {
				continue;
			}

			child.fireComplete();
		}
			
		_completeOut();
	}
	
	@SuppressWarnings("unchecked")
	private <ACCEPTS_OUT extends DataOut> void _completeOut() 
	throws DataException {
		
		((DataNode<?, ?, ACCEPTS_OUT, ?>) getCurrent()).complete((ACCEPTS_OUT) getData());
	}
	
	public void setRunIfTheEnd(RunnableLike runIfTheEnd) {
		this.runIfTheEnd = runIfTheEnd;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ", current=" + current + 
			", state=" + getState();
	}

	abstract class NewBaseWhatNowOut implements Behaviour {
	
		@Override
		public boolean advance(Navigation navigation) throws DataException {
			
			// There is no more data to write, we want to break out
			// of the current loop, but we still want to continue 
			// with any parent to write any trailer record.
			if (Navigation.COMPLETE == navigation && getCurrent() instanceof DataDriver) {
				return false;
			}
			else if (Navigation.IGNORE == navigation) {
				return false;
			}
			
			return _currentOut();
		}
		
		@SuppressWarnings("unchecked")
		private <ACCEPTS_OUT extends DataOut, PROVIDES_OUT extends DataOut> 
		boolean _currentOut() throws DataException {
			 
			WhereNext<DataNode<?, ?, PROVIDES_OUT, ?>, 
				PROVIDES_OUT> whereNext =
				((DataNode<?, ?, ACCEPTS_OUT, PROVIDES_OUT>) getCurrent()).out(
					(ACCEPTS_OUT) getData());
	
				if (whereNext == null) {
					return false;
				}
						
				DataNode<?, ?, ?, ?>[] children = whereNext.getChildren();
				DataOut childData = whereNext.getChildData();
				
				if (children == null) {
					modifyBehaviour(new ProcessedWhatNowOut());
					return true;
				}
				else {
					if (childData == null) {
						throw new NullPointerException("No child data for children " + 
								Arrays.toString(children));
					}
					
					modifyBehaviour(new ProcessingChildren(this, 
							children, childData));
					return true;
				}
		} 
		
		@Override
		public WhatNowOut nextChild() {
			throw new IllegalStateException("Only works when on CHILDREN.");
		}
	
		@Override
		public boolean after() {
			throw new IllegalStateException("Only works when PROCESSED.");
		}
		
		abstract protected void saveChild(WhatNowOut child);
		
		@Override
		public State getState() {
			return State.NEW;
		}
	}


	class RepeatedWhatNowOut extends NewBaseWhatNowOut {
	
		@Override
		public boolean advance(Navigation navigation) throws DataException {
			
			if (super.advance(navigation)) {
				return true;
			}
			else {
				fireComplete();
				theEndOfThisType();
				return false;
			}
		}
		
		@Override
		protected void saveChild(WhatNowOut child) {
			// Only new saves.
		}
	}

	class NewWhatNowOut extends NewBaseWhatNowOut {
	
		@Override
		protected void saveChild(WhatNowOut child) {
			children.add(child);
		}
		
	}

	class ProcessingChildren implements Behaviour {
		
		private Iterator<DataNode<?, ?, ?, ?>> childIterator;
		
		private DataOut childData;
		
		private final NewBaseWhatNowOut previous;
		
		private ProcessingChildren(NewBaseWhatNowOut previous,
				DataNode<?, ?, ?, ?>[] children, DataOut childData) {
			
			if (children == null) {
				throw new NullPointerException("No children.");
			}
			if (childData == null) {
				throw new NullPointerException("No child data.");
			}
			this.childIterator = Arrays.asList(children).iterator();
			this.childData = childData;
			this.previous = previous;
		}
		
		public WhatNowOut nextChild() throws DataException {
			if (childIterator.hasNext()) {
				
				WhatNowOut childWhatNow = 
					new WhatNowOut(childIterator.next(), childData);
				previous.saveChild(childWhatNow);
				
				return childWhatNow;
			}
			else {
				_flush(getData(), childData);
				
				modifyBehaviour(new ProcessedWhatNowOut());
				
				return null;
			}
		}
	
		@SuppressWarnings("unchecked")
		<ACCEPTS_OUT extends DataOut, PROVIDES_OUT extends DataOut>
		void _flush(ACCEPTS_OUT out, PROVIDES_OUT childData)
		throws DataException {
			((DataNode<?, ?, ACCEPTS_OUT, PROVIDES_OUT>) getCurrent()).flush(
					out, childData);
			
		}
		
		@Override
		public boolean after() {
			throw new IllegalStateException("Only works when PROCESSED.");
		}
		
		@Override
		public boolean advance(Navigation navigation)
				throws DataException {
			throw new IllegalStateException("Only works when NEW.");
		}
		
		@Override
		public State getState() {
			return State.CHILDREN;
		}
	}

	class ProcessedWhatNowOut implements Behaviour {
		
	
		@Override
		public WhatNowOut nextChild() {
			throw new IllegalStateException("Only works when on CHILDREN.");
		}
		
		@Override
		public boolean after() throws DataException {
			if (getCurrent() instanceof DataDriver) {
				modifyBehaviour(new RepeatedWhatNowOut());
				return true;
			}
			else {
				theEndOfThisType();
				return false;
			}
		}
		
		@Override
		public boolean advance(Navigation navigation)
				throws DataException {
			throw new IllegalStateException("Only works when NEW.");
		}
				
		@Override
		public State getState() {
			return State.PROCESSED;
		}
	}

}
