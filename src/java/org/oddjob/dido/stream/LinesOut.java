package org.oddjob.dido.stream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataValueOut;

public interface LinesOut extends DataOut, DataValueOut {

	public void writeLine(String text) throws DataException;
}
