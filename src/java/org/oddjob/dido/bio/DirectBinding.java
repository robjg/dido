package org.oddjob.dido.bio;

import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;

/**
 * A very simple {@link Binding} that processes a Java Object out of 
 * or into a single {@link Layout} that is assumed to be a {@link ValueNode}.
 * 
 * @author rob
 *
 */
public class DirectBinding extends SingleBeanBinding
implements Binding {

	@Override
	protected Object extract(Layout node, DataIn dataIn) {

		if (node instanceof ValueNode) {
			return ((ValueNode<?>) node).value();
		}
		else {
			throw new IllegalStateException("Not a Value Node");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void inject(Object object, Layout node, DataOut dataOut) {
		
		if (node instanceof ValueNode) {
			
			ValueNode<Object> valueNode = (ValueNode<Object>) node;
			valueNode.value(object);
		}
		else {
			throw new IllegalStateException("Not a Value Node");
		}
	}
	
	@Override
	public void free() {
	}
}
