package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.stream.LinesOut;
import org.oddjob.dido.tabular.ColumnOut;

/**
 * Implementation for writing out text.
 * 
 * @author rob
 *
 */
public class StringTextOut implements TextOut, LinesOut {

	public static final char PAD_CHARACTER = ' ';
	
	private StringBuilder buffer;

	private boolean writtenTo;
	
	@Override
	public void append(String text) {
		if (buffer == null) {
			buffer = new StringBuilder();
		}
		buffer.append(text);
		writtenTo = true;
	}
	
	@Override
	public void writeLine(String text) throws DataException {
		append(text);
	}

	@Override
	public String lastLine() {
		return toText();
	}
	
	@Override
	public int getLinesWritten() {
		return writtenTo ? 1 : 0;
	}
	
	public void clear() {
		buffer = null;
		writtenTo = false;
	}
	
	public int length() {
		if (buffer == null) {
			return 0;
		}
		else {
			return buffer.length();
		}
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
	throws UnsupportedDataOutException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}

		if (type.isAssignableFrom(TextFieldsOut.class)) {
			TextFieldsOut textFields = new TextFieldsOut() {

				@Override
				public <X extends DataOut> X provideDataOut(Class<X> type)
						throws DataException {
					
					if (type.isInstance(this)) {
						return type.cast(this);
					}

					throw new UnsupportedDataOutException(getClass(), type);
				}

				@Override
				public boolean isWrittenTo()
						throws UnsupportedOperationException {
					return StringTextOut.this.isWrittenTo();
				}

				@Override
				public ColumnOut<String> outFor(Field field) {
					return new ColumnOut<String>() {

						@Override
						public void setData(String data) throws DataException {
							append(data);
						}

						@Override
						public Class<?> getType() {
							return String.class;
						}

						@Override
						public int getColumnIndex() {
							return 1;
						}
						
					};
				}
			};
			
			return type.cast(textFields);					
		}
		
		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + (buffer == null 
				? " unwritten" :  " length " + buffer.length());
	}
	
	@Override
	public boolean isWrittenTo() {
		return writtenTo;
	}
	
	public void resetWrittenTo() {
		writtenTo = false;
	}
	
	@Override
	public boolean isMultiLine() {
		return false;
	}
	
	@Override
	public void close() throws DataException {
		// Nothing to close.
	}
	
	public String toText() {
	
		if (buffer == null) {
			return null;
		}
		else {
			return buffer.toString();
		}
	}
}
