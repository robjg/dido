package org.oddjob.poi;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.AbstractParent;
import org.oddjob.dido.DataException;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;

public class DataSheet 
extends AbstractParent<BookIn, SheetIn, BookOut, SheetOut> {
	private static final Logger logger = Logger.getLogger(DataSheet.class);
	
	private String sheetName;
	
	@Override
	public WhereNextIn<SheetIn> in(
			BookIn data) throws DataException {
		
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
		
		logger.debug("Reading sheet " + sheet.getSheetName());
		
		return new WhereNextIn<SheetIn>(childrenToArray(), 
				dataSheet);
	}
	
	@Override
	public WhereNextOut<SheetOut> out(
			BookOut data) throws DataException {

		Sheet sheet = data.createSheet(sheetName);
		
		PoiSheetOut sheetData = new PoiSheetOut(sheet, 
				data);
		
		logger.debug("Created sheet " + sheet.getSheetName());
		
		return new WhereNextOut<SheetOut>(childrenToArray(), sheetData);
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
