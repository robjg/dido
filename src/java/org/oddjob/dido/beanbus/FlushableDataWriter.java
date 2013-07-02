package org.oddjob.dido.beanbus;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriter;

public interface FlushableDataWriter extends DataWriter {

	public void flush() throws DataException;
}
