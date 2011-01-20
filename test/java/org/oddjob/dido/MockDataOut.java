package org.oddjob.dido;

public class MockDataOut implements DataOut {

	@Override
	public boolean flush() throws DataException {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
}
