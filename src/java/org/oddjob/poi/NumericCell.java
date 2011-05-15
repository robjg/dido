package org.oddjob.poi;

import org.apache.poi.ss.usermodel.Cell;

public class NumericCell extends DataCell<Double> {

	@Override
	public Class<Double> getType() {
		return Double.class;
	}
	
	@Override
	protected int getCellType() {
		return Cell.CELL_TYPE_NUMERIC;
	}

	@Override
	protected void extractCellValue(Cell cell) {
		setValue(cell.getNumericCellValue());
		
	}
	
	@Override
	protected void insertValueInto(Cell cell) {
		Double value = getValue();
		if (value != null) {
			cell.setCellValue(value.doubleValue());
		}
	}
	
	public void setValue(Double value) {
		this.value(value);
	}
	
	public Double getValue() {
		return this.value();
	}
}
