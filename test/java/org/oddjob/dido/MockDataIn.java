package org.oddjob.dido;

public class MockDataIn implements DataIn {

	@Override
	public <T extends DataIn> T provide(Class<T> type)
			throws UnsupportedeDataInException {
		throw new RuntimeException("Unexpected!");
	}
}
