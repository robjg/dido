package dido.poi;

import org.apache.poi.ss.usermodel.Sheet;
import dido.poi.style.StyleProvider;
import dido.poi.style.StyleProviderFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Something that can write data to a spreadsheet workbook.
 * 
 * @author rob
 *
 */
public interface BookOut extends Closeable {
	
	/**
	 * Create a sheet in the workbook.
	 * 
	 * @param name The name of the sheet. Can be null.
	 * 
	 * @return A spreadsheet sheet.
	 */
	Sheet getOrCreateSheet(String name);
	
	/**
	 * Close the workbook. This will be called by a layout to ensure
	 * all data is written out to any output stream.
	 * 
	 */
	@Override
	void close() throws IOException;
	
	/**
	 * Add a style factory to this Style Provider.
	 * 
	 * @param styleProviderFactory The factory used to create the style
	 */
	StyleProvider createStyles(StyleProviderFactory styleProviderFactory);
}
