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

	private final TextFieldHelper textFieldHelper = 
			new TextFieldHelper();
	
	public static final char PAD_CHARACTER = ' ';
	
	private final StringTextOut buffer = new StringTextOut();
	
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
		buffer.clear();
	}
	
	public String getText() {
		return buffer.toText();
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
			
			int from = fixedWidthColumn.getIndex() - 1;
			int length = fixedWidthColumn.getLength();			
						
			buffer.write(text, from, length);
			
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
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": index=" + 
					fixedWidthColumn.getIndex() + ", length=" + 
					fixedWidthColumn.getLength();
		}
	}
	
}
