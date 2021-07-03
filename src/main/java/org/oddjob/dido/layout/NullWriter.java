package org.oddjob.dido.layout;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriter;

public class NullWriter implements DataWriter {

	@Override
	public boolean write(Object value) throws DataException {
		return false;
	}
	
	@Override
	public void close() throws DataException {
	}
}
