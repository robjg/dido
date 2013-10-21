package org.oddjob.dido;

import org.oddjob.dido.bio.Binding;

public class MockLayout implements Layout {
	
	@Override
	public void bind(Binding bindings) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public Iterable<Layout> childLayouts() {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public String getName() {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public void reset() {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn)
			throws UnsupportedDataInException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
			throws UnsupportedDataOutException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
