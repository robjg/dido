package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.poi.style.StyleProvider;
import org.oddjob.dido.poi.style.StyleProviderFactory;

/**
 * Something that can write data to spreadsheet workbook.
 * 
 * @author rob
 *
 */
public interface BookOut extends DataOut, StyleProvider {
	
	/**
	 * Create a sheet in the workbook.
	 * 
	 * @param name The name of the sheet. Can be null.
	 * 
	 * @return A spreadsheet sheet.
	 */
	public Sheet createSheet(String name);
	
	/**
	 * Close the workbook. This will be called by a layout to ensure
	 * all data is written out to any output stream.
	 * 
	 * @throws DataException
	 */
	public void close() throws DataException;
	
	/**
	 * Add a style factory to this Style Provider.
	 * 
	 * @param styleProviderFactory The factory used to create the style
	 */
	public void addStyleFactory(StyleProviderFactory styleProviderFactory);
}
