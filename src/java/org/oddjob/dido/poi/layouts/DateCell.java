package org.oddjob.dido.poi.layouts;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

public class DateCell extends DataCell<Date> {

	public static final String DEFAULT_DATE_STYLE = "date";
	
	@Override
	public Class<Date> getType() {
		return Date.class;
	}
	
	@Override
	protected int getCellType() {
		return Cell.CELL_TYPE_NUMERIC;
	}
	
	@Override
	protected void extractCellValue(Cell cell) {
		setValue(cell.getDateCellValue());
	}
		
	@Override
	protected void insertValueInto(Cell cell) {
		Date value = getValue();
		if (value == null) {
			cell.setCellType(Cell.CELL_TYPE_BLANK);
		}
		else {
			cell.setCellValue(value);
		}
	}
	
	@Override
	public String getDefaultStyle() {
		return DEFAULT_DATE_STYLE;
	}
	
	public void setValue(Date value) {
		this.value(value);
	}
	
	public Date getValue() {
		return this.value();
	}
}
