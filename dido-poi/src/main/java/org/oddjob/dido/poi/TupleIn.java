package org.oddjob.dido.poi;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.TabularDataIn;

/**
 * For reading a group of named values, generally a row.
 * 
 * @author rob
 *
 */
public interface TupleIn extends TabularDataIn {
		
	/**
	 * Provide a incoming cells representation for reading data from.
	 * 
	 * @param column The field definition that is probably a {@link Column}.
	 * 
	 * @return A {@link CellIn}. Never null.
	 */
	@Override
	public CellIn<?> inFor(Field column);
	
}
