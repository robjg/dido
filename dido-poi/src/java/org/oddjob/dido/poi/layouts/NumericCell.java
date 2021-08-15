package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;

public class NumericCell extends DataCell<Double> {

	@Override
	public Class<Double> getType() {
		return Double.class;
	}
	
	@Override
	public int getCellType() {
		return Cell.CELL_TYPE_NUMERIC;
	}

	@Override
	public Double extractCellValue(Cell cell) {
		return cell.getNumericCellValue();
	}
	
	@Override
	public void insertValueInto(Cell cell, Double value) {
		if (value != null) {
			cell.setCellValue(value.doubleValue());
		}
	}
		
	public Double getValue() {
		return this.value();
	}
}
