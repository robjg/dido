package org.oddjob.dido.poi;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.poi.style.StyleProvider;
import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.TabularDataOut;

/**
 * For writing a group of named values, generally a row.
 * 
 * @author rob
 *
 */
public interface TupleOut extends TabularDataOut, StyleProvider {

	/**
	 * Provide an outgoing cells representation for writing data to.
	 * 
	 * @param column The field definition that is probably a {@link Column}.
	 * 
	 * @return A {@link CellOut}. Never null.
	 */
	@Override
	public CellOut<?> outFor(Field column);
}
