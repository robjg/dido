package org.oddjob.dido.field;

import org.oddjob.dido.DataOut;

/**
 * A representation of writable data that can be identified by a {@link Field} 
 * that is normally a type of {@link Layout}.
 * 
 * @author rob
 *
 */
public interface FieldDataOut extends DataOut {

	/**
	 * Provide something that can provide access to the data in the field
	 * based on the given field definition. 
	 * <p>
	 * Implementations may then use the label of the field for 
	 * writing a heading line.
	 * 
	 * @param field The field definition.
	 * 
	 * @return Access to the field data. Never null.
	 */
	public FieldOut<?> outFor(Field column);
}
