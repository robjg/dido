package org.oddjob.dido.layout;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;

public class VoidIn implements DataIn {

	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
}
