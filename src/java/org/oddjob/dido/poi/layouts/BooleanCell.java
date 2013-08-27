package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @oddjob.description Create a column of boolean cells.
 * 
 * @author rob
 *
 */
public class BooleanCell extends DataCell<Boolean> {

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
	
	@Override
	public int getCellType() {
		return Cell.CELL_TYPE_BOOLEAN;
	}
	
	@Override
	public Boolean extractCellValue(Cell cell) {
		return cell.getBooleanCellValue();
	}
		
	@Override
	public void insertValueInto(Cell cell, Boolean value) {
		cell.setCellValue(value);
	}	
	
	/**
	 * @oddjob.property value
	 * @oddjob.description The last value set by this layout.
	 * @oddjob.required Read only.
	 * 
	 * @return
	 */
	public Boolean getValue() {
		return this.value();
	}
}
