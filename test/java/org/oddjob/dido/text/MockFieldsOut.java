package org.oddjob.dido.text;

import org.oddjob.dido.DataException;


public class MockFieldsOut implements FieldsOut {

	@Override
	public int writeHeading(String heading, int column) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public void setColumn(int column, String value) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public boolean flush() throws DataException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
