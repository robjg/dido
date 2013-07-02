package org.oddjob.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedeDataInException;

public class PoiSheetIn implements SheetIn {

	private final Sheet sheet;
	
	private SimpleHeadings headings;
	
	private int rowNum = -1;
	private int columnNum = -1;
		
	private Row row;
	
	public PoiSheetIn(Sheet sheet) {
		this.sheet = sheet;
	}
	
	@Override
	public void startAt(int firstRow, int firstColumn) {
		this.rowNum = firstRow - 1;
		this.columnNum = firstColumn - 1;
	}
	
	@Override
	public boolean headerRow() {
		if (rowNum + 1 < sheet.getLastRowNum()) {
			this.headings = new SimpleHeadings(sheet.getRow(++rowNum), 
					columnNum + 1);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean nextRow() {
		if (rowNum == sheet.getLastRowNum()) {
			return false;
		}
		else {
			row = sheet.getRow(++rowNum);
			return true;
		}
	}

	@Override
	public int getCurrentRow() {
		return rowNum;
	}
	
	@Override
	public int getLastColumn() {
		return columnNum;
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(TupleIn.class)) {
			return type.cast(new PoiTupleIn());
		}
		
		throw new UnsupportedeDataInException(this.getClass(), type);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + sheet.getSheetName();
	}

	/**
	 * 
	 */
	class PoiTupleIn implements TupleIn {
		
		@Override
		public int indexForHeading(String title) {
			
			if (headings != null && title != null) {
				return headings.position(title);
			}
			else {
				return ++columnNum;
			}
		}	
		
		@Override
		public Cell getCell(int column) {
			return row.getCell(column);
		}
		
		@Override
		public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {
			
			if (type.isInstance(this)) {
				return type.cast(this);
			}
			
			throw new UnsupportedeDataInException(this.getClass(), type);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " row [" + 
					row.getRowNum() + "]";
		}
		
	}
}
