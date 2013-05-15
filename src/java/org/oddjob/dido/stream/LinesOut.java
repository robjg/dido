package org.oddjob.dido.stream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataOutProvider;

public interface LinesOut extends DataOut, DataOutProvider {

	public void writeLine(String text) throws DataException;
}
