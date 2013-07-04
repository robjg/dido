package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.poi.style.StyleProvider;

public interface SheetOut extends DataOut, StyleProvider, SheetData {

	public void headerRow(String headingStyle);
	
	public void nextRow();
	
	public Sheet getTheSheet();
}
