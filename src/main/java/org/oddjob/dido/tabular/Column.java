package org.oddjob.dido.tabular;

import org.oddjob.dido.field.Field;

/**
 * Something that represents a column in a tabular data set.
 * 
 * @author rob
 *
 */
public interface Column extends Field {

	/**
	 * Get the index of the column within the tabular data set. This is
	 * always 1 based. If a value of 0 is returned that is interpreted as
	 * this column does not a fixed position and will be assigned an index.
	 * 
	 * @return The index.
	 */
	public int getIndex();
	
}
