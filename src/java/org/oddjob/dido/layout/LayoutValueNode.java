package org.oddjob.dido.layout;

import org.oddjob.dido.ValueNode;


abstract public class LayoutValueNode<T> extends LayoutNode
implements ValueNode<T> {

	/** The nodes value. */
	private T value;

	private boolean writtenTo;
	
	@Override
	public T value() {
		return value;
	}
	
	@Override
	public void value(T value) {
		this.value = value;
		if (value == null) {
			this.writtenTo = false;
		}
		else {
			this.writtenTo = true;
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		value(null);
	}
	
	protected boolean isWrittenTo() {
		return writtenTo;
	}
	
	protected void resetWrittenTo() {
		writtenTo = false;
	}
}
