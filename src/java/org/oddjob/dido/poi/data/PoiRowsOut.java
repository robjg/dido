package org.oddjob.dido.poi.data;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.poi.RowsOut;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.TupleOut;
import org.oddjob.dido.poi.style.DefaultStyleFactory;
import org.oddjob.dido.poi.style.StyleProvider;

/**
 * Implementation of {@link RowsOut}.
 * 
 * @author rob
 *
 */
public class PoiRowsOut implements RowsOut {

	private static final Logger logger = Logger.getLogger(PoiRowsOut.class);

	/** The sheet these rows are being written on. */
	private final Sheet sheet;
	
	/** The style provider. */
	private final StyleProvider styleProvider;
	
	/** The Offset from the first row. Required for auto filtering. */
	private final int rowOffset;

	/** The offset from the first column. Used to calculate cell position
	 * for columns. Also required for auto filtering. */
	private final int columnOffset;
	
	/** The 1 based index of the last row written to. 0 if no row has
	 * been created yet. */
	private int lastRowNum;
	
	/** The 1 based index of the last column requested. 0 if no column
	 * has been requested yet. */
	private int lastColumnNum;
	
	/** The current row being written to. */
	private Row row;
	
	/** The headings row. If any. */
	private Row headings;
	
	/** The name of the heading style. */
	private String headingStyle;
		
	/**
	 * Create a new instance.
	 * 
	 * @param sheetIn
	 * @param firstRow
	 * @param firstColumn
	 */
	public PoiRowsOut(SheetOut sheetIn, 
			int firstRow, int firstColumn) {
		
		if (sheetIn == null) {
			throw new NullPointerException("Sheet.");
		}
		
		this.sheet = sheetIn.getTheSheet();
		this.styleProvider = sheetIn;
		
		if (firstRow < 1) {
			firstRow = 1;
		}
		if (firstColumn < 1) {
			firstColumn = 1;
		}
		
		this.lastRowNum = firstRow - 1;
		this.lastColumnNum = firstColumn - 1; 

		this.rowOffset = lastRowNum;
		this.columnOffset = lastColumnNum; 
	}
	
	@Override
	public void headerRow(String headingStyle) {
		this.headings = sheet.createRow(lastRowNum++);
		this.headingStyle = headingStyle;
		
		logger.debug("Created header row at row ["+ lastRowNum + "].");
	}
	
	@Override
	public void nextRow() {
		
		row = sheet.getRow(lastRowNum);
		if (row == null) {
			row = sheet.createRow(lastRowNum++);
			logger.debug("Created row " + lastRowNum);		
		}
		else {
			++lastRowNum;
			logger.debug("Using row " + lastRowNum);		
		}
	}
	
	@Override
	public int getLastRow() {
		return lastRowNum;
	}
	
	@Override
	public int getLastColumn() {
		return lastColumnNum;
	}
	
	@Override
	public void autoFilter() {
		sheet.setAutoFilter(
				new CellRangeAddress(
						rowOffset, lastRowNum -1,
						columnOffset, lastColumnNum - 1));
	}
	
	@Override
	public void autoWidth() {
		for (int i = 1; i <= lastColumnNum; ++i) {
			sheet.autoSizeColumn(i);
		}
	}
	
	@Override
	public boolean isWrittenTo() {
		throw new RuntimeException("To Do.");
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type) throws DataException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(TupleOut.class)) {
			return type.cast(new PoiTupleOut());
		}
		
		throw new UnsupportedDataOutException(this.getClass(), type);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + sheet.getSheetName();
	}
	
	/**
	 * Implementation of {@link TupleOut}.
	 */
	class PoiTupleOut implements TupleOut {
		
		@Override
		public Cell createCell(int column, int type) {

			if (column < 1) {
				throw new IndexOutOfBoundsException(
						"Column " + column + " is invalid.");
			}
			
			return row.createCell(columnOffset + column - 1, type);
		}
		
		@Override
		public int indexForHeading(String heading) {
			
			if (headings != null) {
				Cell cell = headings.createCell(
						lastColumnNum, Cell.CELL_TYPE_STRING);
				cell.setCellValue(heading);
				String headingStyle = PoiRowsOut.this.headingStyle;
				if (headingStyle == null) {
					headingStyle = DefaultStyleFactory.HEADING_STYLE;
				}
				CellStyle style = styleFor(headingStyle);
				if (style != null) {
					cell.setCellStyle(style);
				}
			}
			
			++lastColumnNum;
			
			return lastColumnNum - columnOffset;
		}
		
		@Override
		public boolean isWrittenTo() {
			throw new RuntimeException("To Do");
		}
		
		@Override
		public CellStyle styleFor(String style) {
			return styleProvider.styleFor(style);
		}
		
		@Override
		public <T extends DataOut> T provideDataOut(Class<T> type) throws DataException {

			if (type.isInstance(this)) {
				return type.cast(this);
			}
			
			throw new UnsupportedDataOutException(this.getClass(), type);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + 
					(row == null ? "(unused)" : 
						" row [" + row.getRowNum() + "]");
		}
	}
}
