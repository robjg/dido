package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.ColumnOut;

/**
 * A collection of fields for writing that will create a line by assembling
 * section of fixed width text.
 * 
 * @author rob
 *
 */
public class FixedWidthTextFieldsOut implements TextFieldsOut {

	private final FixedWidthTextFieldHelper textFieldHelper = 
			new FixedWidthTextFieldHelper();
	
	public static final char PAD_CHARACTER = ' ';
	
	private StringBuilder buffer;

	private boolean writtenTo;
		
	@Override
	public boolean isWrittenTo() {
		return writtenTo;
	}

	public void resetWrittenTo() {
		writtenTo = false;
	}
	
	/**
	 * Clears old fields written for a previous line.
	 */
	public void clear() {
		buffer = null;
	}
	
	public String getText() {
		if (buffer == null) {
			return null;
		}
		else {
			return buffer.toString();
		}
	}
	
	protected void write(String text, int from, int length) {
		if (buffer == null) {
			buffer = new StringBuilder();
		}
		
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
	public ColumnOut<String> outFor(Field field) {
		return new TextColumnOut(
				textFieldHelper.columnIndexFor(field));
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
			
			int from = fixedWidthColumn.getIndex() - 1;
			int length = fixedWidthColumn.getLength();			
						
			write(text, from, length);
		}
		
		@Override
		public Class<?> getType() {
			return String.class;
		}
		
		@Override
		public int getColumnIndex() {
			return fixedWidthColumn.getIndex();
		}		
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": index=" + 
					fixedWidthColumn.getIndex() + ", length=" + 
					fixedWidthColumn.getLength();
		}
	}
	
}
