package org.oddjob.dido.layout;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedeDataInException;

public class VoidIn implements DataIn {

	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {
		
		throw new UnsupportedeDataInException(this.getClass(), type);
	}
}
