package org.oddjob.dido.bio;

import org.oddjob.dido.DataInProvider;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;

public class ValueBinding implements DataBindingIn {

	@Override
	public Object process(Layout node, DataInProvider dataInProvider) {
		if (node instanceof ValueNode) {
			return ((ValueNode<?>) node).value();
		}
		else {
			throw new IllegalStateException("Not a Value Node");
		}
	}
}
