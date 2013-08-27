package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;

public class TextCell extends DataCell<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	@Override
	public int getCellType() {
		return Cell.CELL_TYPE_STRING;
	}
	
	@Override
	public String extractCellValue(Cell cell) {
		return cell.getStringCellValue();
	}

	@Override
	public void insertValueInto(Cell cell, String value) {
		cell.setCellValue(value);
	}
	
	public String getValue() {
		return this.value();
	}
}
