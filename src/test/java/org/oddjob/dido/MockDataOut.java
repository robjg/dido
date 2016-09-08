package org.oddjob.dido;

public class MockDataOut implements DataOut {

	@Override
	public boolean isWrittenTo() {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
			throws UnsupportedDataOutException {
		throw new RuntimeException("Unexpected from " + getClass().getName());
	}
}
