package org.oddjob.dido.text;

import java.util.Arrays;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.ListLinesIn;
import org.oddjob.dido.tabular.ColumnIn;


/**
 * Implementation for reading text in.
 * 
 * @author rob
 *
 */
public class StringTextIn implements TextIn {

	private final String text;
	
	public StringTextIn(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}	
	
	public String toString() {
		return text;
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
			throws UnsupportedDataInException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}

		if (type.isAssignableFrom(LinesIn.class)) {
			return type.cast(new ListLinesIn(Arrays.asList(text)));
		}
		
		if (type.isAssignableFrom(TextFieldsIn.class)) {
			TextFieldsIn textFields = new TextFieldsIn() {

				@Override
				public <X extends DataIn> X provideDataIn(Class<X> type)
						throws DataException {
					
					if (type.isInstance(this)) {
						return type.cast(this);
					}

					throw new UnsupportedDataInException(getClass(), type);
				}

				@Override
				public ColumnIn<String> inFor(Field field) {
					return new ColumnIn<String>() {

						@Override
						public String getData() throws DataException {
							return text;
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
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
}
