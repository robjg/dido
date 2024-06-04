package dido.poi;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * Something the can read data from a spreadsheet workbook.
 * 
 * @author rob
 *
 */
public interface BookIn {

	/**
	 * Get the next sheet in the book.
	 * 
	 * @return A sheet. Will be null if there are no sheets in the workbook.
	 */
	Sheet nextSheet();
	
	/**
	 * Get the named sheet. 
	 * 
	 * @param sheetName
	 * 
	 * @return A sheet. Will be null if the sheet doesn't exist.
	 */
	Sheet getSheet(String sheetName);
}
