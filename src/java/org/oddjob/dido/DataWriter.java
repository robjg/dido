package org.oddjob.dido;

public interface DataWriter {

	public boolean write(Object value) throws DataException;
}
