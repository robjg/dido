package org.oddjob.dido.bio;

import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;

public class ValueBinding implements Binding {

	@Override
	public Object process(Layout node, DataIn dataIn, 
			boolean revist) {

		if (revist) {
			return null;
		}
		
		if (node instanceof ValueNode) {
			return ((ValueNode<?>) node).value();
		}
		else {
			throw new IllegalStateException("Not a Value Node");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean process(Object object, Layout node, DataOut dataOut) {
		
		if (node instanceof ValueNode) {
			
			ValueNode<Object> valueNode = (ValueNode<Object>) node;
			valueNode.value(object);
			
			return false;
		}
		else {
			throw new IllegalStateException("Not a Value Node");
		}
	}
	
	@Override
	public void reset() {
	}
}
