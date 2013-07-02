package org.oddjob.dido;

public class MockDataIn implements DataIn {

	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
			throws UnsupportedeDataInException {
		throw new RuntimeException("Unexpected!");
	}
}
