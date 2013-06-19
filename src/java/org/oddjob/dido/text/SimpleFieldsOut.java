package org.oddjob.dido.text;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;

public class SimpleFieldsOut implements FieldsOut {

	private Map<Integer, String> headings;
	
	private Map<Integer, String> values;
	
	private int maxColumn;
			
	private boolean writtenTo;
	
	public SimpleFieldsOut() {
		this(null);
	}
	
	public SimpleFieldsOut(String[] headings) {

		if (headings != null) {
			
			for (String heading : headings) {
				
				columnForHeading(heading, 0);
			}
		}
	}
	
	@Override
	public int columnForHeading(String heading, int column) {
		
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
	public <T extends DataOut> T provide(Class<T> type)
			throws UnsupportedeDataOutException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}

		throw new UnsupportedeDataOutException(getClass(), type);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + 
				(values == null ? "no" : values.size()) + "] values";
	}
}
