package org.oddjob.dido.text;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.column.Column;
import org.oddjob.dido.column.ColumnIn;

public class SimpleFieldsIn
implements FieldsIn {

	private Map<String, Integer> headerToColumn;
	
	private String[] values;
	
	private int lastColumn = 0;
	
	public void setHeadings(String[] headings) {

		if (headings == null) {
			throw new NullPointerException("Null Headings.");
		}
		
		headerToColumn = new HashMap<String, Integer>();

		for (int i = 0; i < headings.length; ++i) {
			headerToColumn.put(headings[i], i + 1);
		}		
	}
	
	class TextColumnIn implements ColumnIn<String> {
		
		private final int columnIndex;
		
		public TextColumnIn(int columnIndex) {
			this.columnIndex = columnIndex;
		}
		
		@Override
		public int getColumnIndex() {
			return columnIndex;
		}
		
		@Override
		public Class<String> getColumnType() {
			return String.class;
		}
		
		@Override
		public String getColumnData() throws DataException {
			if (columnIndex == 0 || columnIndex > values.length) {
				return null;
			}
			return values[columnIndex - 1];
		}
	}
	
	public void setValues(String[] values) {
		this.values = values;
	}

	@Override
	public ColumnIn<String> columnInFor(Column column) {

		String heading = column.getColumnLabel();		
		int columnIndex = column.getColumnIndex();
		int useColumnIndex = 0;
		
		if (headerToColumn != null && heading != null) {
			
			Integer headerColumn = headerToColumn.get(heading);
			
			if (headerColumn == null) {
				useColumnIndex = columnIndex;
			}
			else {
				useColumnIndex = headerColumn.intValue();
			}
		}
		else {
			useColumnIndex = columnIndex;
			
			if (useColumnIndex == 0) {
				useColumnIndex = ++lastColumn;
			}
			else {
				lastColumn = useColumnIndex;
			}
		}
		
		return new TextColumnIn(useColumnIndex);
	}
		
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
	throws UnsupportedeDataInException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		throw new UnsupportedeDataInException(this.getClass(), type);
	}
}
