package org.oddjob.dido.layout;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;

public class NullReader implements DataReader {

	@Override
	public Object read() throws DataException {
		return null;
	}
	
	@Override
	public void close() throws DataException {
	}
}
