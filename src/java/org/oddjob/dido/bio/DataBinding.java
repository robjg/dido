package org.oddjob.dido.bio;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataInProvider;
import org.oddjob.dido.DataOutProvider;
import org.oddjob.dido.Layout;

public interface DataBinding {

	public Object process(Layout node, DataInProvider dataIn, 
			boolean revist)
	throws DataException;
	
	public boolean process(Object value, 
			Layout node, DataOutProvider dataOut)
	throws DataException;
	
	public void close();
}
