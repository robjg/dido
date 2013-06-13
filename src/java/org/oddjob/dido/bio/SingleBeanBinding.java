package org.oddjob.dido.bio;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;

/**
 * Base implementation for Bindings that only return one Object per
 * item of data read or write one item of data for each object written.
 * 
 * @author rob
 *
 */
abstract public class SingleBeanBinding implements Binding {

	@Override
	public DataReader readerFor(final Layout boundLayout, final DataIn dataIn)
			throws DataException {
		
		return new DataReader() {
			
			/** Binding of this type where there is a one Object for one 
			 * Layout will be recalled with the same data. On the second call 
			 * the {@code revist} flag will be true so the reader may simply 
			 * return null
			 */
			 private boolean revisit = false;
			
			@Override
			public Object read() throws DataException {
				
				if (revisit) {
					return null;
				}
				
				try {
					return extract(boundLayout, dataIn);
				}
				finally {
					revisit = true;
				}
			}
			
			@Override
			public void close() throws DataException {
				
				// Todo: Binding needs to be closeable.
			}
			
			@Override
			public String toString() {
				
				return "BindingReader for " + boundLayout;
			}
		};
	}
	
	protected abstract Object extract(Layout boundLayout, DataIn dataIn)
	throws DataException;
	
	
	@Override
	public DataWriter writerFor(final Layout boundLayout, final DataOut dataOut)
	throws DataException {
		
		return new DataWriter() {
			@Override
			public boolean write(Object value) throws DataException {
				return inject(value, boundLayout, dataOut);
			}
			
			@Override
			public void close() throws DataException {
				
				// Todo: Binding need to be closeable.
			}
			
			public String toString() {
			
				return "BindingWriter for " + boundLayout;
			}
		};
	}
	
	protected abstract boolean inject(Object object, 
			Layout boundLayout, DataOut dataOut)
	throws DataException;
	
}
