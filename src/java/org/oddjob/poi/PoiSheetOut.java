package org.oddjob.poi;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;

public class PoiSheetOut implements SheetOut {

	private static final Logger logger = Logger.getLogger(PoiSheetOut.class);

	private final Sheet sheet;
	
	private final StyleProvider styleProvider;
	
	private int rowNum = -1;
	private int columnNum = -1;
	
	private Row row;
	private Row headings;
	
	private String headingStyle;
	
	public PoiSheetOut(Sheet sheet) {
		this(sheet, new StyleProvider() {
			
			@Override
			public CellStyle styleFor(String style) {
				return null;
			}
		});
	}	
	
	public PoiSheetOut(Sheet sheet, StyleProvider styleProvider) {
		this.sheet = sheet;
		this.styleProvider = styleProvider;
	}
	
	@Override
	public void startAt(int firstRow, int firstColumn) {
		this.rowNum = firstRow - 1;
		this.columnNum = firstColumn - 1; 
	}
	
	@Override
	public void headerRow(String headingStyle) {
		this.headings = sheet.createRow(++rowNum);
		this.headingStyle = headingStyle;
		
		logger.debug("Created header row at row ["+ rowNum + "].");
	}
	
	@Override
	public void nextRow() {
		this.row = sheet.createRow(++rowNum);
		
		logger.debug("Created row " + rowNum);
	}
	
	@Override
	public CellStyle styleFor(String style) {
		return styleProvider.styleFor(style);
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
	public Sheet getTheSheet() {
		return sheet;
	}
	
	@Override
	public boolean isWrittenTo() {
		throw new RuntimeException("To Do.");
	}
	
	@Override
	public <T extends DataOut> T provide(Class<T> type) throws DataException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(TupleOut.class)) {
			return type.cast(new PoiTupleOut());
		}
		
		throw new UnsupportedeDataOutException(this.getClass(), type);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + sheet.getSheetName();
	}
	
	/**
	 * 
	 */
	class PoiTupleOut implements TupleOut {
		
		@Override
		public Cell createCell(int column, int type) {
			return row.createCell(column, type);
		}
		
		@Override
		public int indexForHeading(String heading) {
			
			++columnNum;
			
			if (headings != null) {
				Cell cell = headings.createCell(
						columnNum, Cell.CELL_TYPE_STRING);
				cell.setCellValue(heading);
				String headingStyle = PoiSheetOut.this.headingStyle;
				if (headingStyle == null) {
					headingStyle = DefaultStyleFactory.HEADING_STYLE;
				}
				CellStyle style = styleFor(headingStyle);
				if (style != null) {
					cell.setCellStyle(style);
				}
			}
			return columnNum;
		}
		
		@Override
		public boolean isWrittenTo() {
			throw new RuntimeException("To Do");
		}
		
		@Override
		public CellStyle styleFor(String style) {
			return PoiSheetOut.this.styleFor(style);
		}
		
		@Override
		public <T extends DataOut> T provide(Class<T> type) throws DataException {

			if (type.isInstance(this)) {
				return type.cast(this);
			}
			
			throw new UnsupportedeDataOutException(this.getClass(), type);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + 
					(row == null ? "(unused)" : 
						" row [" + row.getRowNum() + "]");
		}
	}
}
