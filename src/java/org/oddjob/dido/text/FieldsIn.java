package org.oddjob.dido.text;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.ColumnIn;
import org.oddjob.dido.tabular.TabularDataIn;

/**
 * Readable tabular data that always provides text columns.
 * 
 * @author rob
 *
 */
public interface FieldsIn extends TabularDataIn {

	@Override
	public ColumnIn<String> inFor(Field column);
	
}
