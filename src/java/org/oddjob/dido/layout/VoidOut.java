package org.oddjob.dido.layout;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;

public class VoidOut implements DataOut {

	@Override
	public boolean isWrittenTo() {
		return false;
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type) throws DataException {
		
		throw new UnsupportedDataOutException(getClass(), type);
	}
}
