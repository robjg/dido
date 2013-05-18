package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;

 
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
	
	@Override
	public <T extends DataOut> T provide(Class<T> type)
			throws UnsupportedeDataOutException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	@Override
	public boolean hasData() {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	@Override
	public <T> T toValue(Class<T> type) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
