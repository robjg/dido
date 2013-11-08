package org.oddjob.dido.field;

import java.util.Map;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;

public class SimpleFieldDataIn implements FieldDataIn {

	private Map<String, String> values;
	
	public void setValues(Map<String, String> values) {
		this.values = values;
	}
	
	public Map<String, String> getValues() {
		return values;
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
			throws DataException {
		
		if (type.isAssignableFrom(this.getClass())) {
			return type.cast(this);
		}

		throw new UnsupportedDataInException(getClass(), type);
	}
	
	@Override
	public FieldIn<String> inFor(Field field) {
		
		NamedField fieldImpl = new NamedField(field.getLabel());
		
		
		return fieldImpl;
	}

	class NamedField implements FieldIn<String> {
		
		private final String label;
		
		public NamedField(String label) {
			this.label = label;
		}
		
		@Override
		public String getData() throws DataException {
			return values.get(label);
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
