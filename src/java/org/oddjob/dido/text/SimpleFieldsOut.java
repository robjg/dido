package org.oddjob.dido.text;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;

public class SimpleFieldsOut implements FieldsOut {

	private Map<Integer, String> headings;
	
	private Map<Integer, String> values;
	
	private int maxColumn;
			
	public SimpleFieldsOut() {
		this(null);
	}
	
	public SimpleFieldsOut(String[] headings) {

		if (headings != null) {
			
			for (String heading : headings) {
				
				writeHeading(heading, 0);
			}
		}
	}
	
	@Override
	public int writeHeading(String heading, int column) {
		
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
	
	@Override
	public void setColumn(int column, String value) {
		
		if (column < 1) {
			throw new IllegalArgumentException("Column is " + column);
		}
		
		if (values == null) {
			values = new HashMap<Integer, String>();
		}
		
		values.put(column, value);
	}
	
	@Override
	public boolean flush() throws DataException {
		return true;
	}
	
	private static String[] toArray(Map<Integer, String> things) {
		if (things == null) {
			return null;
		}
		
		int size = 0;
		for (int i : things.keySet() ) {
			if (i > size) {
				size = i;
			}
		}
		String[] a = new String[size];
		
		for (int i = 0; i < size; ++i) {
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
	}
	
	@Override
	public <T extends DataOut> T provideOut(Class<T> type)
			throws UnsupportedeDataOutException {
		if (FieldsOut.class.isAssignableFrom(type)) {
			return type.cast(this);
		}
		else {
			throw new UnsupportedeDataOutException(getClass(), type);
		}
	}

	@Override
	public boolean hasData() {
		return values != null;
	}
	
	@Override
	public <T> T toValue(Class<T> type) {
		return type.cast(values());
	}
}
