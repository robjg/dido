package org.oddjob.dido.poi;

import org.oddjob.dido.DataIn;

public interface SheetIn extends DataIn, SheetData {

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
	
}
