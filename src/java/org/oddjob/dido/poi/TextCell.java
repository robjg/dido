package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Cell;

public class TextCell extends DataCell<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	@Override
	protected int getCellType() {
		return Cell.CELL_TYPE_STRING;
	}
	
	@Override
	protected void extractCellValue(Cell cell) {
		setValue(cell.getStringCellValue());
	}

	@Override
	protected void insertValueInto(Cell cell) {
		cell.setCellValue(getValue());
	}
	
	public void setValue(String value) {
		this.value(value);
	}
	
	public String getValue() {
		return this.value();
	}
}
