package org.oddjob.dido.text;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.column.Column;
import org.oddjob.dido.column.ColumnOut;
import org.oddjob.dido.column.ColumnarDataOut;

/**
 * Provide {@link ColumnarDataOut} for {@link DelimitedLayout}.
 * 
 * @author rob
 *
 */
public class SimpleFieldsOut implements FieldsOut {

	private final Map<String, Integer> indexForHeading;
	
	private final Map<Integer, String> headings = 
			new HashMap<Integer, String>();
	
	private final Map<Integer, String> values = 
			new TreeMap<Integer, String>();
	
	private int lastColumn;
			
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
	 * @param headings May be null
	 */
	public SimpleFieldsOut(String[] headings) {
		if (headings == null) {
			
			indexForHeading = null;
		}
		else {
			
			indexForHeading = new HashMap<String, Integer>();
					
			for (int i = 0; i < headings.length; ++i) {
				
				Integer columnIndex = new Integer(i + 1);
				String heading = headings[i];
				this.indexForHeading.put(heading, columnIndex);
				this.headings.put(columnIndex, heading);
			}
		}
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
		public Class<String> getColumnType() {
			return String.class;
		}
		
		@Override
		public void setColumnData(String data) throws DataException {
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
	}
	
	
	@Override
	public ColumnOut<String> columnOutFor(Column column) {
				
		String heading = column.getColumnLabel();
		int columnIndex = column.getColumnIndex();
		int useColumnIndex = 0;
		
		if (indexForHeading != null && heading != null) {
			
			Integer headingColumn = indexForHeading.get(heading);
			if (headingColumn == null) {
				useColumnIndex = columnIndex;
			}
			else {
				useColumnIndex = headingColumn.intValue();
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

		if (heading != null && useColumnIndex > 0) {
			headings.put(useColumnIndex, heading);
		}
		
		return new TextColumnOut(lastColumn);
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
		
		String[] a = new String[maxColumn ];
		
		for (int i = 0; i < maxColumn; ++i) {
			String thing = things.get(i + 1);
			a[i] = thing;
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
		values.clear();
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
