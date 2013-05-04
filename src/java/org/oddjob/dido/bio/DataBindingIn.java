package org.oddjob.dido.bio;

import org.oddjob.dido.DataInProvider;
import org.oddjob.dido.Layout;

public interface DataBindingIn {

	public Object process(Layout node, DataInProvider dataIn);
	
}
