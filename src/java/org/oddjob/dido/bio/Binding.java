package org.oddjob.dido.bio;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;

public interface Binding {

	public Object process(Layout node, DataIn dataIn, 
			boolean revist)
	throws DataException;
	
	/**
	 * Process a value into the out.
	 * 
	 * @param value The value to be written.
	 * @param node The node that this binding is bound to.
	 * @param dataOut The data that will be written to.
	 * 
	 * @return Is more data required. If true then any calling node should
	 * return to client code to be given a new value. If false calling code
	 * should progress on to the next node in the sequence of walking the
	 * layout hierarchy.
	 * 
	 * @throws DataException
	 */
	public boolean process(Object value, 
			Layout node, DataOut dataOut)
	throws DataException;
	
	public void close();
}
