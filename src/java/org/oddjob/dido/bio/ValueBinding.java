package org.oddjob.dido.bio;

import org.oddjob.dido.DataInProvider;
import org.oddjob.dido.DataOutProvider;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;

public class ValueBinding implements DataBinding {

	@Override
	public Object process(Layout node, DataInProvider dataInProvider, 
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
	public boolean process(Object value, Layout node, DataOutProvider dataOut) {
		if (node instanceof ValueNode) {
			ValueNode<Object> valueNode = (ValueNode<Object>) node;
			valueNode.value(value);
			return false;
		}
		else {
			throw new IllegalStateException("Not a Value Node");
		}
	}
	
	@Override
	public void close() {
	}
}
