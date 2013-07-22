package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;

public class BlankCell extends DataCell<Void> {


	@Override
	public Class<Void> getType() {
		return Void.class;
	}

	@Override
	protected int getCellType() {
		return Cell.CELL_TYPE_BLANK;
	}

	@Override
	protected void extractCellValue(Cell cell) {
		this.value(null);
	}
	
	@Override
	protected void insertValueInto(Cell cell) {
		// Nothing to do here.
	}
}
