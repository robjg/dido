package org.oddjob.dido.poi.layouts;

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
		value(cell.getBooleanCellValue());
	}
		
	@Override
	protected void insertValueInto(Cell cell) {
		cell.setCellValue(getValue());
	}	
	
	public Boolean getValue() {
		return this.value();
	}
}
