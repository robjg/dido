package org.oddjob.dido.text;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;

abstract class BaseFieldsOut implements FieldsOut {

	private Map<Integer, String> values;
	
	private final FieldsWriter writer;

	public BaseFieldsOut(FieldsWriter writer) {
		this.writer = writer; 
	}
	
	protected void setValue(int column, String value) {
		if (column < 1) {
			throw new IllegalArgumentException("Column is " + column);
		}
		
		if (values == null) {
			values = new HashMap<Integer, String>();
		}
		
		values.put(column, value);
	}
	
	@Override
	abstract public int writeHeading(String heading, int column);
	
	@Override
	abstract public void setColumn(int column, String value);
	
	@Override
	public boolean flush() throws DataException {
		if (values != null) {
			writer.write(toArray(values));
			values = null;
			return true;
		}
		return false;
	}
	
	public interface FieldsWriter {
		public void write(String[] values) throws DataException;
	}
	
	private static String[] toArray(Map<Integer, String> things) {
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
	
	@Override
	public <T extends DataOut> T provide(Class<T> type)
			throws UnsupportedeDataOutException {
		if (FieldsOut.class.isAssignableFrom(type)) {
			return type.cast(this);
		}
		else {
			throw new UnsupportedeDataOutException(getClass(), type);
		}
	}
}
