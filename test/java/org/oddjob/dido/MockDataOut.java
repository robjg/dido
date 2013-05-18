package org.oddjob.dido;

public class MockDataOut implements DataOut {

	@Override
	public boolean flush() throws DataException {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
	@Override
	public boolean hasData() {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
	@Override
	public <T extends DataOut> T provide(Class<T> type)
			throws UnsupportedeDataOutException {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
	@Override
	public <T> T toValue(Class<T> type) {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
}
