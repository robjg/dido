package org.oddjob.dido;

public class MockDataOut implements DataOut {

	@Override
	public boolean flush() throws DataException {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
	@Override
	public boolean hasData() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public <T extends DataOut> T provideOut(Class<T> type)
			throws UnsupportedeDataOutException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T> T toValue(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}
}
