package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.StringTextOut;

/**
 * @oddjob.description Read to and from a series of cells. Currently only 
 * the ability to read columns of data by nesting within a {@link DataRows}
 * is supported but the ability to read rows will be added at some point.
 * 
 * @author rob
 *
 */
public class TextCell extends DataCell<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	@Override
	public int getCellType() {
		return Cell.CELL_TYPE_STRING;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public String extractCellValue(Cell cell) {
		
		// We need this because even if the cell is a formatted cell
		// but contains a number we get a can't read from numeric cell
		// exception.
		if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
		}
		return cell.getStringCellValue();
	}

	@Override
	protected DataIn childDataIn() {
		return new StringTextIn(value());
	}
	
	@Override
	public void insertValueInto(Cell cell, String value) {
		cell.setCellValue(value);
	}

	@Override
	public DataOutControl<String> childDataOut() {
		
		return new DataOutControl<String>() {
			
			StringTextOut textOut = new StringTextOut();
			
			@Override
			public DataOut dataOut() {
				return textOut;
			}
			@Override
			public boolean isWrittenTo() {
				return textOut.isMultiLine();
			}
			@Override
			public void resetWrittenTo() {
				textOut.resetWrittenTo();
			}
			@Override
			public String value() {
				return textOut.toText();
			}
		};
	}
	
	public String getValue() {
		return this.value();
	}
}
