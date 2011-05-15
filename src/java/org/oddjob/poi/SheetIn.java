package org.oddjob.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.dido.DataIn;

public interface SheetIn extends DataIn, SheetData {

	public Cell getCell(int column);
	
	/**
	 * Read a header row.
	 * 
	 * @return True if there is a header row , false if there
	 * isn't.
	 */
	public boolean headerRow();
	
	/**
	 * Advance the current row. 
	 * 
	 * @return True if there is another row , false if there
	 * isn't.
	 */
	public boolean nextRow();
	
	public int columnFor(String title);
}
