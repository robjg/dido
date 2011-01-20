package org.oddjob.dido;


public final class VoidData implements DataIn, DataOut {

	private VoidData() {}
	
	@Override
	public boolean flush() throws DataException {
		return false;
	}
}
