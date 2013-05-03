package org.oddjob.dido.io;

import org.oddjob.dido.DataException;

public interface DataReader {

	public Object read() throws DataException;
}
