package org.oddjob.dido.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.oddjob.dido.DataDriver;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.SupportsChildren;
import org.oddjob.dido.WhereNext;
import org.oddjob.dido.WhereNextIn;

/**
 * An internal class that tracks where a reader is in the parser tree.
 * 
 * @author rob
 *
 */
abstract class WhatNowIn {

	enum State{
		NEW,
		CHILDREN,
		PROCESSED
	}
	
	private DataNode<?, ?,?, ?> current;
	
	private DataIn data; 
	
	private LinkInControl control;
	
	protected RunnableLike runIfTheEnd;
	
	protected WhatNowIn(WhatNowIn existing) {
		this(existing.current, existing.data);
		this.control = existing.control;
	}
	
	protected WhatNowIn(DataNode<?, ?, ?, ?> current, 
			DataIn data) {
		if (current == null) {
			throw new NullPointerException("No Current Node.");
		}
		if (data == null) {
			throw new NullPointerException("No Current Data.");
		}
		this.current = current;
		this.data = data;
	}
	
	abstract List<WhatNowIn> nextChild();

	public DataNode<?, ?, ?, ?> getCurrent() {
		return current;
	}

	public DataIn getData() {
		return data;
	};
	
	abstract public State getState();
	
	abstract public WhatNowIn getAfter();
	
	public WhatNowIn currentIn() throws DataException {		
		throw new IllegalStateException("Only works when NEW.");
	}
	
	protected final void theEndOfThisType() throws DataException {
		if (runIfTheEnd != null) {
			runIfTheEnd.run();
		}
	}

	public void setRunIfTheEnd(RunnableLike runIfTheEnd) {
		throw new IllegalStateException("Only works when on PROCESSED.");
	}
	
	public LinkInControl getControl() {
		return control;
	}

	public void setControl(LinkInControl control) {
		this.control = control;
	}
	
	public String toString() {
		return getClass().getSimpleName() + ": " + current;
	}
	
}

abstract class NewBaseWhatNowIn extends WhatNowIn {

	protected NewBaseWhatNowIn(DataNode<?, ?, ?, ?> current, DataIn data) {
		super(current, data);
	
	}
	
	protected <ACCEPTS_IN extends DataIn, PROVIDES_IN extends DataIn> 
	WhatNowIn whatThen(
			WhereNextIn<PROVIDES_IN> where) {
		if (where == null) {
			return null;
		}
				
		DataNode<?, ?, ?, ?>[] children = where.getChildren();
		DataIn childData = where.getChildData();
		
		if (children == null) {
			return new ProcessedWhatNowIn(this);
		}
		else {
			if (childData == null) {
				throw new NullPointerException("No child data for children " + 
						Arrays.toString(children));
			}
			
			return new ProcessingChildrenIn(this, children, 
					childData);
		}
	}

	@Override
	public WhatNowIn currentIn() throws DataException {
		return _currentIn();
	}
	
	@SuppressWarnings("unchecked")
	protected <ACCEPTS_IN extends DataIn, PROVIDES_IN extends DataIn> 
	WhatNowIn _currentIn() throws DataException {
		 
		WhereNextIn<PROVIDES_IN> whereNext =
			((DataNode<ACCEPTS_IN, PROVIDES_IN, ?, ?>) getCurrent()).in(
				(ACCEPTS_IN) getData());

		return whatThen(whereNext);
	}
	
	@Override
	public List<WhatNowIn> nextChild() {
		throw new IllegalStateException("Only works when on CHILDREN.");
	}

	@Override
	public WhatNowIn getAfter() {
		throw new IllegalStateException("Only works when PROCESSED.");
	}
	
	@Override
	public State getState() {
		return State.NEW;
	}
}


class RepeatedWhatNowIn extends NewBaseWhatNowIn {

	protected RepeatedWhatNowIn(WhatNowIn previousWhatNowIn) {
		super(previousWhatNowIn.getCurrent(), previousWhatNowIn.getData());
		this.runIfTheEnd = previousWhatNowIn.runIfTheEnd;
	}
	
	@Override
	public WhatNowIn currentIn() throws DataException {
		WhatNowIn next = super.currentIn();
		
		if (next == null) {
			if (getCurrent() instanceof DataDriver) {
				fireComplete(getCurrent());
			}			
			theEndOfThisType();
		}
		
		return next;
	}
	
	private void fireComplete(DataNode<?, ?, ?, ?> node) throws DataException {
		if (node instanceof SupportsChildren) {
			DataNode<?, ?, ?, ?>[] children = 
				((SupportsChildren) node).childrenToArray();
			
			for (int i = children.length; i > 0; ) {
				DataNode<?, ?, ?, ?> child = children[--i];
				if (child instanceof DataDriver) {
					continue;
				}
				fireComplete(child);
			}
		}
		_completeIn(getData());
	}
	
	@SuppressWarnings("unchecked")
	private <ACCEPTS_IN extends DataIn> void _completeIn(ACCEPTS_IN data) 
	throws DataException {
		((DataNode<ACCEPTS_IN, ?, ?, ?>) getCurrent()).complete(data);
	}
}

class NewWhatNowIn extends NewBaseWhatNowIn {

	public NewWhatNowIn(DataNode<?, ?, ?, ?> current, DataIn data) {
		super(current, data);	
	}

}

class ProcessingChildrenIn extends WhatNowIn {
	
	private Iterator<DataNode<?, ?, ?, ?>> childIterator;
	
	private DataIn childData;
	
	public ProcessingChildrenIn(NewBaseWhatNowIn existing,
			DataNode<?, ?, ?, ?>[] children, DataIn childData) {
		super(existing);
		if (children == null) {
			throw new NullPointerException("No children.");
		}
		if (childData == null) {
			throw new NullPointerException("No child data.");
		}
		this.childIterator = Arrays.asList(children).iterator();
		this.childData = childData; 
	}
	
	public List<WhatNowIn> nextChild() {
		List<WhatNowIn> next = new ArrayList<WhatNowIn>();
		if (childIterator.hasNext()) {
			next.add(this);
			WhatNowIn nextIn = new NewWhatNowIn(
					childIterator.next(), childData);
			next.add(nextIn);
		}
		else {
			WhatNowIn whatNow = new ProcessedWhatNowIn(this);
			next.add(whatNow);
		}
		return next;
	}

	@Override
	public WhatNowIn getAfter() {
		throw new IllegalStateException("Only works when PROCESSED.");
	}
		
	@Override
	public WhatNowIn currentIn()
			throws DataException {
		throw new IllegalStateException("Only works when NEW.");
	}
	
	@Override
	public State getState() {
		return State.CHILDREN;
	}
}

class ProcessedWhatNowIn extends WhatNowIn {
	
	public ProcessedWhatNowIn(WhatNowIn existing) {
		super(existing);
	}

	@Override
	public List<WhatNowIn> nextChild() {
		throw new IllegalStateException("Only works when on CHILDREN.");
	}
	
	@Override
	public WhatNowIn getAfter() {
		if (getCurrent() instanceof DataDriver) {
			return new RepeatedWhatNowIn(this);
		}
		else {
			return null;
		}
	}
	
	@Override
	public WhatNowIn currentIn()
			throws DataException {
		throw new IllegalStateException("Only works when NEW.");
	}
	
	@Override
	public void setRunIfTheEnd(RunnableLike runIfTheEnd) {
		this.runIfTheEnd = runIfTheEnd;
	}
	
	@Override
	public State getState() {
		return State.PROCESSED;
	}
}

