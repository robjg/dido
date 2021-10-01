package org.oddjob.dido.poi.data;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.oddjob.dido.poi.HeaderRowOut;
import org.oddjob.dido.poi.RowOut;
import org.oddjob.dido.poi.RowsOut;
import org.oddjob.dido.poi.style.DefaultStyleProivderFactory;
import org.oddjob.dido.poi.style.StyleProvider;

import java.util.Objects;

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

	private int maxColumn;

	/** The current row being written to. */
	private Row row;

	/**
	 * Create a new instance.
	 * 
	 * @param sheet The POI worksheet
	 * @param firstRow The index of the first row.
	 * @param firstColumn The index of the first column.
	 */
	public PoiRowsOut(Sheet sheet, StyleProvider styleProvider, int firstRow, int firstColumn) {
		
		this.sheet = Objects.requireNonNull(sheet, "No sheet.");
		this.styleProvider = Objects.requireNonNull(styleProvider, "No Style Provider");
		
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
	public HeaderRowOut headerRow(String headingStyleIn) {

		Row headings;
		if (sheet.getLastRowNum() > lastRowNum) {
			headings = sheet.getRow(lastRowNum);
		}
		else {
			headings = sheet.createRow(lastRowNum);
		}
		++lastRowNum;
		logger.debug("Created header row at row ["+ lastRowNum + "].");

		return (columnIndex, heading) -> {

			Cell cell = headings.createCell(
					columnIndex - 1 + columnOffset, CellType.STRING);
			cell.setCellValue(heading);

			String headingStyle = headingStyleIn;
			if (headingStyle == null) {
				headingStyle = DefaultStyleProivderFactory.HEADING_STYLE;
			}

			CellStyle style = styleProvider.styleFor(headingStyle);
			if (style != null) {
				cell.setCellStyle(style);
			}
			maxColumn = Math.max(maxColumn, columnIndex);
		};
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
	public RowOut getRowOut() {
		return new RowOut() {
			@Override
			public Cell getCell(int columnIndex, CellType poiColumnType) {
				int poiCellIndex = columnOffset + columnIndex - 1;
				maxColumn = Math.max(maxColumn, columnIndex);
				return row.createCell(poiCellIndex, poiColumnType);
			}

			@Override
			public CellStyle styleFor(String styleName) {
				return styleProvider.styleFor(styleName);
			}
		};
	}

	@Override
	public int getLastRow() {
		return lastRowNum;
	}

	@Override
	public int getLastColumn() {
		return maxColumn + columnOffset;
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

	public <T> Cell getCell(int columnIndex, CellType poiColumnType) {
			int poiCellIndex = columnOffset + columnIndex - 1;

			return row.createCell(poiCellIndex, poiColumnType);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + sheet.getSheetName();
	}

}
