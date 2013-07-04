package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.poi.style.StyleProvider;

public interface BookOut extends DataOut, StyleProvider {
	
	public Sheet createSheet(String name);
	
	public void close() throws DataException;
}
