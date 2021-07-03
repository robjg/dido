package org.oddjob.dido.field;

import org.oddjob.dido.DataException;

/**
 * Something that can write data data to a {@link Field}.
 * 
 * @author rob
 *
 * @param <T> The type of the field.
 */
public interface FieldOut<T> extends FieldData {

	/**
	 * Write the data.
	 * 
	 * @param data The data. Can be null.
	 * 
	 * @throws DataException
	 */
	public void setData(T data)
	throws DataException;
}
