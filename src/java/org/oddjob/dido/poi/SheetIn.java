package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataIn;

/**
 * Provide a way of reading a sheet.
 * 
 * @author rob
 *
 */
public interface SheetIn extends DataIn {

	/**
	 * Get the sheet this represents.
	 * 
	 * @return A sheet. Never null.
	 */
	public Sheet getTheSheet();

}
