package org.oddjob.dido.field;

import org.oddjob.dido.DataException;

/**
 * Something that can read the data from a {@link Field}.
 * 
 * @author rob
 *
 * @param <T> The type of the field.
 */
public interface FieldIn<T> extends FieldData {

	/**
	 * Get the data from the field.
	 * 
	 * @return The value. May be null.
	 * 
	 * @throws DataException
	 */
	public T getData()
	throws DataException;
}
