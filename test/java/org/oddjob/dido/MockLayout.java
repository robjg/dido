package org.oddjob.dido;

import java.util.List;

import org.oddjob.dido.bio.Binding;

public class MockLayout implements Layout {
	
	@Override
	public void bind(Binding bindings) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public List<Layout> childLayouts() {
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
			throws UnsupportedeDataInException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
			throws UnsupportedeDataOutException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
