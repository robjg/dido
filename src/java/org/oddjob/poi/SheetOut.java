package org.oddjob.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataOut;

public interface SheetOut extends DataOut, StyleProvider, SheetData {

	public Cell createCell(int column, int type);
	
	public void headerRow(String headingStyle);
	
	public void nextRow();
	
	public int writeHeading(String heading);	
	
	public Sheet getTheSheet();
}
