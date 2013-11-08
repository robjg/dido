package org.oddjob.dido.field;

import java.util.LinkedHashMap;
import java.util.Map;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;

public class SimpleFieldDataOut implements FieldDataOut {

	private final Map<String, String> values =
			new LinkedHashMap<String, String>();
	
	public Map<String, String> getValues() {
		return new LinkedHashMap<String, String>(values);
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
	throws DataException {
		
		if (type.isAssignableFrom(this.getClass())) {
			return type.cast(this);
		}

		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public boolean isWrittenTo() throws UnsupportedOperationException {
		return !values.isEmpty();
	}
	
	public void clear() {
		values.clear();
	}
	
	@Override
	public FieldOut<String> outFor(Field field) {
		
		NamedField fieldImpl = new NamedField(field.getLabel());
				
		return fieldImpl;
	}

	class NamedField implements FieldOut<String> {
		
		private final String label;
		
		public NamedField(String label) {
			this.label = label;
		}

		@Override
		public void setData(String data) throws DataException {
			if (data != null) {
				values.put(label, data);
			}
		}
		
		@Override
		public Class<?> getType() {
			return String.class;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + label;
		}
	}
	
	
}
