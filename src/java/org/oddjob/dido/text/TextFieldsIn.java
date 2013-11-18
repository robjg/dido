package org.oddjob.dido.text;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.ColumnIn;
import org.oddjob.dido.tabular.TabularDataIn;

/**
 * Readable tabular data that always provides text columns.
 * 
 * @author rob
 *
 */
public interface TextFieldsIn extends TabularDataIn {

	
	/**
	 * Provide access for reading from a text column.
	 * 
	 * @param field The field definition. This will often be a {@link Column}.
	 * 
	 * @return The text column assigned.
	 */
	@Override
	public ColumnIn<String> inFor(Field field);
	
}
