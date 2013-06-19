package org.oddjob.dido.stream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;

public interface LinesOut extends DataOut {

	public void writeLine(String text) throws DataException;
	
	public String lastLine();
	
	public void resetWrittenTo();
	
	public boolean isMultiLine();
}
