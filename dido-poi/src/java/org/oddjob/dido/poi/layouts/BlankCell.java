package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @oddjob.description Create a column cells that are blank. Provided for
 * completeness - not sure of the use case for this.
 * 
 * @author rob
 *
 */
public class BlankCell extends DataCell<Void> {

	@Override
	public Class<Void> getType() {
		return Void.class;
	}

	@Override
	public int getCellType() {
		return Cell.CELL_TYPE_BLANK;
	}

	@Override
	public Void extractCellValue(Cell cell) {
		return null;
	}
	
	@Override
	public void insertValueInto(Cell cell, Void value) {
		// Nothing to do here.
	}
}
