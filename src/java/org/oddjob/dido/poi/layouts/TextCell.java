package org.oddjob.dido.poi.layouts;

import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.StringTextOut;

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
