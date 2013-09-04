package org.oddjob.dido.tabular;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.field.FieldDataIn;

/**
 * An abstraction for data that is to be read from a layout that
 * is in tabular form.  
 * 
 * @author rob
 *
 */
public interface TabularDataIn extends FieldDataIn {

	/**
	 * Provide access to the data for the given field definition.
	 * <p>
	 * @param column The field definition. This is often a {@link Column}.
	 * 
	 * @return Access to the column data.
	 */
	public ColumnIn<?> inFor(Field column);

}
