package org.oddjob.dido.text;

import java.util.Map;
import java.util.TreeMap;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.ColumnHelper;
import org.oddjob.dido.tabular.ColumnOut;

/**
 * Provide a {@link TextFieldsOut} for a {@link DelimitedLayout}.
 * 
 * @author rob
 *
 */
public class SimpleTextFieldsOut implements TextFieldsOut, StringsOut {

	private final ColumnHelper columnHelper;
	
	private final Map<Integer, String> values = 
			new TreeMap<Integer, String>();
	
	private boolean writtenTo;
	
	/**
	 * Create an instance with no headings.
	 */
	public SimpleTextFieldsOut() {
		this(null);
	}
	
	/**
	 * Create an instance with the given headings.
	 * 
	 * @param headings May be null
	 */
	public SimpleTextFieldsOut(String[] headings) {
		this.columnHelper = new ColumnHelper(headings);
	}

	class TextColumnOut implements ColumnOut<String> {

		private final int columnIndex;
		
		public TextColumnOut(int index) {
			this.columnIndex = index;
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
		public void setData(String data) throws DataException {
			if (columnIndex > 0) {
				if (data == null) {
					values.remove(columnIndex);
				}
				else {
					values.put(columnIndex, data);
					writtenTo = true;
				}
			}
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + columnIndex;
		}
	}
	
	
	@Override
	public ColumnOut<String> outFor(Field column) {
				
		return new TextColumnOut(columnHelper.columnIndexFor(column));
	}
	
	/**
	 * Get the headings. 
	 * 
	 * @return An array, Null if there are no headings.
	 */
	public String[] headings() {
		return columnHelper.getHeadings();
	}
	
	@Override
	public void setValues(String[] values) {
		for (int columnIndex = 1; columnIndex <= values.length; ++columnIndex) {
			String data = values[columnIndex - 1];
			if (data == null) {
				this.values.remove(columnIndex);
			}
			else {
				this.values.put(columnIndex, data);
				writtenTo = true;
			}
		}
	}
	
	/**
	 * Get the values. 
	 * 
	 * @return An array, Never null.
	 */
	public String[] values() {
		return ColumnHelper.toArray(values);
	}
	
	public void clear() {
		values.clear();
		resetWrittenTo();
	}
	
	@Override
	public boolean isWrittenTo() {
		return writtenTo;
	}
	
	/**
	 * Reset the written to flag.
	 */
	public void resetWrittenTo() {
		this.writtenTo = false;
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
			throws UnsupportedDataOutException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}

		throw new UnsupportedDataOutException(getClass(), type);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + 
				(values == null ? "no" : values.size()) + "] values";
	}
}
