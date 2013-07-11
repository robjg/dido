package org.oddjob.dido.text;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.column.ColumnMetaData;
import org.oddjob.dido.column.ColumnarDataOut;

/**
 * Provide {@link ColumnarDataOut} for {@link DelimitedLayout}.
 * 
 * @author rob
 *
 */
public class SimpleFieldsOut implements FieldsOut {

	private Map<Integer, String> headings;
	
	private Map<Integer, String> values;
	
	private int maxColumn;
			
	private boolean writtenTo;
	
	/**
	 * Create an instance with no headings.
	 */
	public SimpleFieldsOut() {
		this(null);
	}
	
	/**
	 * Create an instance with the given headings.
	 * 
	 * @param headings
	 */
	public SimpleFieldsOut(String[] headings) {

		if (headings != null) {
			
			for (String heading : headings) {
				
				columnIndexFor(heading, 0);
			}
		}
	}
	
	
	
	@Override
	public int columnIndexFor(String heading, int column) {
		
		if (column < 1) {
			column = maxColumn + 1;
		}
		if (column > maxColumn) {
			maxColumn = column;
		}
		
		if (heading != null) {			
			if (headings == null) {				
				headings = new HashMap<Integer, String>();
			}
			
			headings.put(column, heading);
		}
		
		return column;
	}
	
	public ColumnMetaData getColumnMetaData(int columnIndex) {
		return new ColumnMetaData() {
			
			@SuppressWarnings("unchecked")
			@Override
			public <T> Class<T> getColumnType() {
				return (Class<T>) String.class;
			}
		};
	}
	
	@Override
	public void setColumnData(int column, String value) {
		
		if (column < 1) {
			throw new IllegalArgumentException("Column is " + column);
		}
		
		if (values == null) {
			values = new HashMap<Integer, String>();
		}
		
		values.put(column, (String) value);
		writtenTo = true;
	}
	
	private static String[] toArray(Map<Integer, String> things) {
		if (things == null) {
			return null;
		}
		
		int maxColumn = 0;
		for (Integer i : things.keySet() ) {
			if (i.intValue() > maxColumn) {
				maxColumn = i.intValue();
			}
		}
		String[] a = new String[maxColumn];
		
		for (int i = 0; i < maxColumn; ++i) {
			String thing = things.get(i + 1);
			if (thing != null) {
				a[i] = thing;
			}
		}
		
		return a;
	}
	
	public String[] headings() {
		return toArray(headings);
	}
	
	public String[] values() {
		return toArray(values);
	}
	
	public void clear() {
		values = null;
		resetWrittenTo();
	}
	
	@Override
	public boolean isWrittenTo() {
		return writtenTo;
	}
	
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
