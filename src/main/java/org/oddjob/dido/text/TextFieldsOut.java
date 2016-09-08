package org.oddjob.dido.text;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.ColumnOut;
import org.oddjob.dido.tabular.TabularDataOut;

/**
 * A type of {@link TabularDataOut} where every column is text.
 * 
 * @author rob
 *
 */
public interface TextFieldsOut extends TabularDataOut {

	/**
	 * Provide access for writing to a text column.
	 * 
	 * @param field The field definition. This will often be a {@link Column}.
	 * 
	 * @return The text column assigned.
	 */
	@Override
	public ColumnOut<String> outFor(Field field);
}
