package org.oddjob.dido.poi.data;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.poi.CellIn;
import org.oddjob.dido.poi.RowsIn;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.TupleIn;
import org.oddjob.dido.poi.utils.CellHelper;
import org.oddjob.dido.tabular.ColumnHelper;

/**
 * Implementation of {@link RowsIn}.
 * 
 * @author rob
 *
 */
public class PoiRowsIn implements RowsIn {

	private final Sheet sheet;
	
	private final ColumnHelper columnHelper = new ColumnHelper();
	
	private SimpleHeadings headings;
	
	/** The offset from the first column. Used to calculate cell position
	 * for columns. */
	private final int columnOffset; 
	
	/** The 1 based index of the last row written. */
	private int lastRowNum;
	
	/** The current row. */
	private Row row;
	
	/**
	 * Create an instance.
	 * 
	 * @param sheetIn
	 * @param firstRow
	 * @param firstColumn
	 */
	public PoiRowsIn(SheetIn sheetIn, int firstRow, int firstColumn) {
		this.sheet = sheetIn.getTheSheet();
		
		if (firstRow < 1) {
			firstRow = 1;
		}
		if (firstColumn < 1) {
			firstColumn = 1;
		}
		
		this.lastRowNum = firstRow - 1;
		
		this.columnOffset = firstColumn - 1;
	}
	
	@Override
	public boolean headerRow() {
		
		Row row = sheet.getRow(lastRowNum);
		
		if (row == null) {
			return false;
		}
		else {
			this.headings = new SimpleHeadings(row, columnOffset);
			columnHelper.setHeadings(headings.getHeadings());
			
			++lastRowNum;
			return true;
		}
	}
	
	@Override
	public boolean nextRow() {
		
		row = sheet.getRow(lastRowNum);
		if (row == null) {
			return false;
		}
		else {
			++lastRowNum;
			return true;
		}
	}

	@Override
	public int getLastRow() {
		return lastRowNum;
	}
	
	@Override
	public int getLastColumn() {
//		return lastColumnNum;
		return columnHelper.getMaxColumn() + columnOffset;
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		if (type.isAssignableFrom(TupleIn.class)) {
			return type.cast(new PoiTupleIn());
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + sheet.getSheetName();
	}
	
	
	/**
	 * Implementation of {@link TupleIn}.
	 */
	class PoiTupleIn implements TupleIn {
		
		@Override
		public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {
			
			if (type.isInstance(this)) {
				return type.cast(this);
			}
			
			throw new UnsupportedDataInException(this.getClass(), type);
		}
		
		@Override
		public CellIn<?> inFor(Field column) {

			final int columnIndex = columnHelper.columnIndexFor(column);
			
			if (column instanceof CellLayout) {
				return createCellInWithInferredType(columnIndex, 
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

	abstract class PoiCellIn<T> implements CellIn<T> {
		
		private final int columnIndex;
		
		private String cellReference;
		
		public PoiCellIn(int columnIndex) {
			this.columnIndex = columnIndex;
		}
		
		@Override
		public int getColumnIndex() {
			return columnIndex;
		}
		
		@Override
		public T getData() throws DataException {

			if (columnIndex == 0) {
				return null;
			}
			else {
				int poiCellIndex = columnOffset + columnIndex - 1;
				
				Cell cell = row.getCell(poiCellIndex);
				
				cellReference = new CellReference(
						cell.getRowIndex(), cell.getColumnIndex()
							).formatAsString();
				
				return getCellValue(cell);
			}
		}
		
		@Override
		public String getCellReference() {
			return cellReference;
		}
		
		abstract protected T getCellValue(Cell cell)
		throws DataException;
		
	}
	
	class GenericCell<T> extends PoiCellIn<T>{
		
		private final Class<T> type;
				
		public GenericCell(int columnIndex, Class<T> type) {
			super(columnIndex);
			this.type = type;
		}
		
		@Override
		public Class<?> getType() {
			return Object.class;
		}
		
		@Override
		protected T getCellValue(Cell cell) throws DataException {
			return new CellHelper().getCellValue(cell, type);
		}
	}
	
	<T> DataCellIn<T> createCellInWithInferredType(int columnIndex, 
			CellLayout<T> dataCell) {
		return new DataCellIn<T>(columnIndex, dataCell);
	}
	
	class DataCellIn<T> extends PoiCellIn<T> {
		
		private final CellLayout<T> dataCell;
		
		public DataCellIn(int columnIndex, CellLayout<T> dataCell) {
			super(columnIndex);
			this.dataCell = dataCell;
		}
		
		@Override
		public Class<?> getType() {
			return dataCell.getType();
		}

		@Override
		protected T getCellValue(Cell cell) throws DataException {

			return dataCell.extractCellValue(cell);
		}
	}		
}
