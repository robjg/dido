package org.oddjob.dido.field;

/**
 * Something that provides access to a field either for reading or writing.
 * 
 * @author rob
 *
 */
public interface FieldData {

	/**
	 * The type of data being provided access to. If in the current data
	 * the field does not exist then a type of Void may be returned.
	 * 
	 * @return The type. Never null.
	 */
	public Class<?> getType();

}
