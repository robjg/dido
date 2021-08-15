package org.oddjob.dido.poi.utils;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Helps with cell setting and getting based on cell type.
 * 
 * @author rob
 *
 */
public class CellHelper {

	public <T> T getCellValue(Cell cell, Class<T> type) {
		
		if (cell == null) {
			throw new NullPointerException();
		}
		
		Object value = null;
		
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_BOOLEAN:
			value = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			value = cell.getCellFormula();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (Date.class.isAssignableFrom(type)) {
				value = cell.getDateCellValue();
			}
			else {
				value = cell.getNumericCellValue();
			}
			break;
		case Cell.CELL_TYPE_ERROR:
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		default:
			throw new IllegalArgumentException("Unknown Cell Type " + 
					cell.getCellType());
		}
		
		return type.cast(value);
	}
	
	public void setCellValue(final Cell cell, final Object value) {
		
		if (value == null) {
			cell.setCellType(Cell.CELL_TYPE_BLANK);
		}
		else {
			new CellTypeFactory<Void>() {

				@Override
				protected Void createNumeric() {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Double) value);
					return null;
				}

				@Override
				protected Void createBoolean() {
					cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
					cell.setCellValue((Boolean) value);
					return null;
				}

				@Override
				protected Void createText() {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue((String) value);
					return null;
				}

				@Override
				protected Void createDate() {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Date) value);
					return null;
				}
				
			}.createFor(value.getClass());
			
		}
	}
}
