package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.ColumnOut;

public class TextFieldsOut implements FieldsOut {

	private final TextFieldHelper textFieldHelper = 
			new TextFieldHelper();
	
	public static final char PAD_CHARACTER = ' ';
	
	private StringBuilder buffer = new StringBuilder();

	private boolean writtenTo;
		
	@Override
	public boolean isWrittenTo() {
		return writtenTo;
	}

	public void resetWrittenTo() {
		writtenTo = false;
	}
	
	public void clear() {
		buffer = new StringBuilder();
	}
	
	public String getText() {
		return buffer.toString();
	}
	
	@Override
	public ColumnOut<String> outFor(Field column) {
		return new TextColumnOut(
				textFieldHelper.columnIndexFor(column));
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
			throws UnsupportedDataOutException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}

		throw new UnsupportedDataOutException(getClass(), type);
	}
		
	class TextColumnOut implements ColumnOut<String> {
		
		private final FixedWidthColumn fixedWidthColumn;
		
		public TextColumnOut(FixedWidthColumn fixedWidthColumn) {
			this.fixedWidthColumn = fixedWidthColumn;
		}
		
		@Override
		public void setData(String text) throws DataException {
			
			int from = fixedWidthColumn.getIndex();
			int length = fixedWidthColumn.getLength();			
						
			while (buffer.length() < from) {
				buffer.append(PAD_CHARACTER);
			}
			
			StringBuilder minibuf;
			
			if (length < 0 || length > text.length()) {
				minibuf = new StringBuilder(text);
			} 
			else {
				minibuf = new StringBuilder(text.substring(0, length));			
			}
			
			while (minibuf.length() < length) {
				minibuf.append(PAD_CHARACTER);
			}
			
			if (from < buffer.length() ) {
				buffer.replace(from, from + minibuf.length(), minibuf.toString());
			}
			else {
				buffer.append(minibuf.toString());
			}
			
			writtenTo = true;
		}
		
		@Override
		public Class<?> getType() {
			return String.class;
		}
		
		@Override
		public int getColumnIndex() {
			return fixedWidthColumn.getIndex();
		}		
	}
	
}
