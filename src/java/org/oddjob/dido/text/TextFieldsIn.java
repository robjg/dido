package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.ColumnIn;

public class TextFieldsIn implements FieldsIn {

	private final TextFieldHelper textFieldHelper = 
			new TextFieldHelper();
	
	private String text;	
	
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
	throws UnsupportedDataInException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
	
	@Override
	public ColumnIn<String> inFor(Field column) {
		return new TextColumnIn(
				textFieldHelper.columnIndexFor(column));
	}
	
	class TextColumnIn implements ColumnIn<String> {
		
		private final FixedWidthColumn fixedWidthColumn;
		
		public TextColumnIn(FixedWidthColumn fixedWidthColumn) {
			this.fixedWidthColumn = fixedWidthColumn;
		}
		
		@Override
		public String getData() throws DataException {
			
			int from = fixedWidthColumn.getIndex() - 1;
			int length = fixedWidthColumn.getLength();
			
			String newText;
			
			if (from > text.length()) {
				newText = "";
			}
			else if (length > 0) {
				int to = from + length;
				if (to > text.length()) {
					to = text.length();
				}
				newText = text.substring(from, to);			
			}
			else {
				newText = text.substring(from);
			}
			
			return newText;
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
