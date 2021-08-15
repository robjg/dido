package org.oddjob.dido.poi.data;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.poi.CellOut;
import org.oddjob.dido.poi.RowsOut;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.TupleOut;
import org.oddjob.dido.poi.style.DefaultStyleProivderFactory;
import org.oddjob.dido.poi.style.StyleProvider;
import org.oddjob.dido.poi.utils.CellHelper;
import org.oddjob.dido.tabular.ColumnHelper;

/**
 * Implementation of {@link RowsOut}.
 * 
 * @author rob
 *
 */
public class PoiRowsOut implements RowsOut {

	private static final Logger logger = Logger.getLogger(PoiRowsOut.class);

	private final ColumnHelper columnHelper = new ColumnHelper();
	
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

		this.rowOffset = lastRowNum;
		this.columnOffset = firstColumn - 1; 
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
		return columnHelper.getMaxColumn() + columnOffset;
	}
	
	@Override
	public void autoFilter() {
		int lastRowNumber = getLastRow() ;
		int lastColumnNumber = getLastColumn();
		
		if (lastRowNumber > 0 && lastColumnNumber > 0) {
			sheet.setAutoFilter(
				new CellRangeAddress(
						rowOffset, lastRowNumber - 1,
						columnOffset, lastColumnNumber - 1));
		}
	}
	
	@Override
	public void autoWidth() {
		int lastColumnNumber = getLastColumn();
		for (int i = columnOffset; i < lastColumnNumber; ++i) {
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
	
	private void writeHeaderForColumn(int columnIndex, String heading) {
		
		if (headings == null) {
			return;
		}
		
		Cell cell = headings.createCell(
				columnIndex - 1 + columnOffset, Cell.CELL_TYPE_STRING);
		cell.setCellValue(heading);
		
		String headingStyle = PoiRowsOut.this.headingStyle;
		if (headingStyle == null) {
			headingStyle = DefaultStyleProivderFactory.HEADING_STYLE;
		}
		
		CellStyle style = styleProvider.styleFor(headingStyle);
		if (style != null) {
			cell.setCellStyle(style);
		}
	}
	
	/**
	 * Implementation of {@link TupleOut}.
	 */
	class PoiTupleOut implements TupleOut {
		
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
		public CellOut<?> outFor(Field column) {

			final int columnIndex = columnHelper.columnIndexFor(column);
			
			if (columnIndex > 0) {
				writeHeaderForColumn(columnIndex, column.getLabel());
			}
			
			if (column instanceof CellLayout) {
				return createCellOutWithInferredType(columnIndex, 
						(CellLayout<?>) column);
			}
			else if (column instanceof ValueNode) {
				return new GenericCell(columnIndex, ((ValueNode) column).getType());
			}
			else {
				return new GenericCell(columnIndex, Object.class);
			}
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + 
					(row == null ? "(unused)" : 
						" row [" + row.getRowNum() + "]");
		}
	}
	
	abstract class PoiCellOut<T> implements CellOut<T> {
		
		private final int columnIndex;
		
		private String cellReference;
		
		public PoiCellOut(int columnIndex) {
			this.columnIndex = columnIndex;
		}
		
		@Override
		public int getColumnIndex() {
			return columnIndex;
		}
		
		@Override
		public void setData(T data) throws DataException {
			
			if (columnIndex != 0) {
				int poiCellIndex = columnOffset + columnIndex - 1;
				
				Cell cell = row.createCell(poiCellIndex, getPoiColumnType());
				
				cellReference = new CellReference(
						cell.getRowIndex(), cell.getColumnIndex()
							).formatAsString();
				
				setCellValue(cell, data);
			}
		}
		
		@Override
		public String getCellReference() {
			return cellReference;
		}
		
		abstract protected int getPoiColumnType();
		
		abstract protected void setCellValue(Cell cell, T value)
		throws DataException;
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": index=" + columnIndex;
		}
	}
	
	class GenericCell<T> extends PoiCellOut<T>{
		
		private final Class<T> type;
		
		public GenericCell(int columnIndex, Class<T> type) {
			super(columnIndex);
			
			this.type = type;
		}
		
		@Override
		public Class<?> getType() {
			return type;
		}
		
		@Override
		protected int getPoiColumnType() {
			return Cell.CELL_TYPE_BLANK;
		}
		
		@Override
		protected void setCellValue(Cell cell, Object value) {
			if (value != null) {
				new CellHelper().setCellValue(cell, value);
			}
		}
	}
	
	<T> DataCellOut<T> createCellOutWithInferredType(int columnIndex, 
			CellLayout<T> dataCell) {
		return new DataCellOut<T>(columnIndex, dataCell);
	}
	
	class DataCellOut<T> extends PoiCellOut<T> {
		
		private final CellLayout<T> dataCell;
		
		public DataCellOut(int columnIndex, CellLayout<T> dataCell) {
			super(columnIndex);
			this.dataCell = dataCell;
		}
		
		@Override
		public Class<?> getType() {
			return dataCell.getType();
		}
		
		@Override
		protected int getPoiColumnType() {
			return dataCell.getCellType();
		}
		
		@Override
		protected void setCellValue(Cell cell, T value) throws DataException {
		
			String style = dataCell.getStyle();
			
			if (style == null) {
				style = dataCell.getDefaultStyle();
			}
			if (style != null) {
				CellStyle cellStyle = styleProvider.styleFor(style);
				
				if (cellStyle == null) {
					throw new DataException("No style available of name [" + 
							style + "] from cell [" + dataCell+ "]");
				}
				
				cell.setCellStyle(cellStyle);
			}
			
			dataCell.insertValueInto(cell, value);
		}
	}
}
