package org.oddjob.poi;

import org.apache.poi.ss.usermodel.Cell;

public class BooleanCell extends DataCell<Boolean> {

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
	
	@Override
	protected int getCellType() {
		return Cell.CELL_TYPE_BOOLEAN;
	}
	
	@Override
	protected void extractCellValue(Cell cell) {
		setValue(cell.getBooleanCellValue());
	}
		
	@Override
	protected void insertValueInto(Cell cell) {
		cell.setCellValue(getValue());
	}	
	
	public void setValue(Boolean value) {
		this.value(value);
	}
	
	public Boolean getValue() {
		return this.value();
	}
}
