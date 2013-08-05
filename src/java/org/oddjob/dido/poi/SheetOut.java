package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.poi.style.StyleProvider;

/**
 * Provide a way of writing a sheet on a spreadsheet.
 * 
 * @author rob
 *
 */
public interface SheetOut extends DataOut, StyleProvider {

	/**
	 * Get the sheet this data represents.
	 * 
	 * @return A sheet. Never null.
	 */
	public Sheet getTheSheet();	
}
