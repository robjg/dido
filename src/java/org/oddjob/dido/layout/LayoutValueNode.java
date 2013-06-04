package org.oddjob.dido.layout;

import org.oddjob.dido.ValueNode;


abstract public class LayoutValueNode<T> extends LayoutNode
implements ValueNode<T> {

	/** The stencils value. */
	private T value;

	@Override
	public T value() {
		return value;
	}
	
	@Override
	public void value(T value) {
		this.value = value;
	}
	
	@Override
	public void reset() {
		super.reset();
		value(null);
	}
}
