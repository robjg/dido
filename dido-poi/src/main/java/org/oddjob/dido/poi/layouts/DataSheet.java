package org.oddjob.dido.poi.layouts;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutNode;
import org.oddjob.dido.layout.NullReader;
import org.oddjob.dido.poi.BookIn;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.data.PoiSheetIn;
import org.oddjob.dido.poi.data.PoiSheetOut;

/**
 * @oddjob.description Define a Spreadsheet sheet for reading or
 * writing to.
 * <p>
 * This layout will not create it's own children based on a binding type. 
 * If this is required then nest a {@link DataRows} layout and bind to
 * that.
 * 
 * @author rob
 *
 */
public class DataSheet extends LayoutNode {
	
	private static final Logger logger = Logger.getLogger(DataSheet.class);
	
	/** @oddjob.property
	 *  @oddjob.description The name of the sheet to read or write. When
	 *  reading if a name is given and the sheet doesn't exist in the
	 *  workbook then no data will be read.
	 *  @oddjob.required No. If not supplied the next sheet is used.
	 */
	private String sheetName;
	
	/**
	 * @oddjob.property of
	 * @oddjob.description The child layouts of this sheet.
	 * @oddjob.required No, but pointless if missing.
	 * 
	 * @param index 0 based index.
	 * @param child The child, null will remove the child for the given index.
	 */
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);	
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {

		BookIn data = dataIn.provideDataIn(BookIn.class);
		
		Sheet sheet;
		if (sheetName == null) {
			sheet = data.nextSheet();
			if (sheet == null) {
				logger.info("[" + this + "] no more sheets.");
				return new NullReader();
			}
		}
		else {
			sheet = data.getSheet(sheetName);
			if (sheet == null) {
				logger.info("[" + this + "] no sheet of name [" + sheetName + "].");
				return new NullReader();
			}
		}
		
		PoiSheetIn dataSheet = new PoiSheetIn(sheet);
		
		logger.debug("Reading sheet " + sheet.getSheetName() + 
				" of rows " + sheet.getLastRowNum());
		
		return nextReaderFor(dataSheet);
	}

	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		final BookOut data = dataOut.provideDataOut(BookOut.class);

		Sheet sheet = data.createSheet(sheetName);
		
		final PoiSheetOut sheetData = new PoiSheetOut(sheet, 
				data);
		
		logger.debug("Created sheet " + sheet.getSheetName());
		
		final DataWriter nextWriter = nextWriterFor(sheetData);
		
		return new DataWriter() {
			
			@Override
			public boolean write(Object object) throws DataException {
				return nextWriter.write(object);
			}
			
			@Override
			public void close() throws DataException {
				nextWriter.close();
				data.close();
			}
		};
	}

	@Override
	public void reset() {
		super.reset();
	}
	
	/**
	 * Getter for sheet name.
	 * 
	 * @return
	 */
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * Setter for sheet name.
	 * 
	 * @param sheetName
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	@Override
	public String toString() {
		String name = getName();
		return getClass().getSimpleName() + 
			(name == null ? "" : " " + name);
	}
}
