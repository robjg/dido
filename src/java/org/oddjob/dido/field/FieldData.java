package org.oddjob.dido.field;

/**
 * Something that provides access to a field either for reading or writing.
 * 
 * @author rob
 *
 */
public interface FieldData {

	/**
	 * The type of data being provided access to.
	 * 
	 * @return The type. Never null.
	 */
	public Class<?> getType();

}
