package org.oddjob.dido.poi.data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.poi.RowIn;
import org.oddjob.dido.poi.RowsIn;

import java.util.Objects;

/**
 * Implementation of {@link RowsIn}.
 * 
 * @author rob
 *
 */
public class PoiRowsIn implements RowsIn {

	private final Sheet sheet;

	/** The offset from the first column. Used to calculate cell position
	 * for columns. */
	private final int columnOffset; 
	
	/** The 1 based index of the last row written. */
	private int lastRowNum;
	
	/**
	 * Create an instance.
	 * 
	 * @param sheet The Work Sheet.
	 * @param firstRow The first row to read data from.
	 * @param firstColumn The first column to read data from.
	 */
	public PoiRowsIn(Sheet sheet, int firstRow, int firstColumn) {
		this.sheet = Objects.requireNonNull(sheet);
		
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
	public String[] headerRow() {
		
		Row row = sheet.getRow(lastRowNum);
		
		if (row == null) {
			return null;
		}
		else {
			SimpleHeadings headings1 = new SimpleHeadings(row, columnOffset);
			String[] headings = headings1.getHeadings();

			++lastRowNum;
			return headings;
		}
	}
	
	@Override
	public RowIn nextRow() {
		
		Row row = sheet.getRow(lastRowNum);
		if (row == null) {
			return null;
		}
		else {
			++lastRowNum;
			return new PoiRowIn(row);
		}
	}

	@Override
	public RowIn peekRow() {
		Row row = sheet.getRow(lastRowNum);
		if (row == null) {
			return null;
		}
		else {
			return new PoiRowIn(row);
		}
	}

	@Override
	public int getLastRow() {
		return lastRowNum;
	}

	class PoiRowIn implements RowIn {

		private final Row row;

		PoiRowIn(Row row) {
			this.row = Objects.requireNonNull((row), "No row. Check nextRow() returned true.");
		}

		@Override
		public Cell getCell(int columnIndex) {
			int poiCellIndex = columnOffset + columnIndex - 1;

			return row.getCell(poiCellIndex);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + sheet.getSheetName();
	}


}
