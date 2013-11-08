package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.ColumnHelper;
import org.oddjob.dido.tabular.ColumnIn;

/**
 * A simple implementation of {@link FieldsIn}.
 * 
 * @author rob
 *
 */
public class SimpleFieldsIn
implements FieldsIn, StringsIn {

	private final ColumnHelper columnHelper = new ColumnHelper();
	
	private String[] values;
	
	public void setHeadings(String[] headings) {
		columnHelper.setHeadings(headings);
	}
	
	class TextColumn implements ColumnIn<String> {
		
		private final int columnIndex;
		
		public TextColumn(int columnIndex) {
			this.columnIndex = columnIndex;
		}
		
		@Override
		public int getColumnIndex() {
			return columnIndex;
		}
		
		@Override
		public Class<String> getType() {
			return String.class;
		}
		
		@Override
		public String getData() throws DataException {
			if (columnIndex == 0 || columnIndex > values.length) {
				return null;
			}
			return values[columnIndex - 1];
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + columnIndex;
		}
	}
	
	public void setValues(String[] values) {
		this.values = values;
	}

	@Override
	public String[] getValues() {
		return this.values;
	}
	
	@Override
	public ColumnIn<String> inFor(Field column) {

		return new TextColumn(columnHelper.columnIndexFor(column));
	}
		
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
	throws UnsupportedDataInException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
}
