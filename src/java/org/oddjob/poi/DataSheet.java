package org.oddjob.poi;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutNode;

public class DataSheet extends LayoutNode {
	
	private static final Logger logger = Logger.getLogger(DataSheet.class);
	
	private String sheetName;
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);	
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {

		BookIn data = dataIn.provideDataIn(BookIn.class);
		
		Sheet sheet;
		if (sheetName == null) {
			sheet = data.nextSheet();
		}
		else {
			sheet = data.getSheet(sheetName);
		}
		if (sheet == null) {
			return null;		
		}
		
		PoiSheetIn dataSheet = new PoiSheetIn(sheet);
		
		logger.debug("Reading sheet " + sheet.getSheetName() + 
				" of rows " + sheet.getLastRowNum());
		
		return nextReaderFor(dataSheet);
	}

	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		BookOut data = dataOut.provideDataOut(BookOut.class);

		Sheet sheet = data.createSheet(sheetName);
		
		PoiSheetOut sheetData = new PoiSheetOut(sheet, 
				data);
		
		logger.debug("Created sheet " + sheet.getSheetName());
		
		return nextWriterFor(sheetData);
	}

	@Override
	public void reset() {
		super.reset();
		
		sheetName = null;
	}
	
	public String getSheetName() {
		return sheetName;
	}

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
