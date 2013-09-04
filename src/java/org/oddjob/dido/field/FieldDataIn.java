package org.oddjob.dido.field;

import org.oddjob.dido.DataIn;

/**
 * A representation of readable data that can be identified by a {@link Field} 
 * that is normally a type of {@link Layout}.
 * 
 * @author rob
 *
 */
public interface FieldDataIn extends DataIn {

	/**
	 * Provide something that can provide access to the data in the field
	 * based on the given field definition. 
	 * <p>
	 * 
	 * @param field The field definition.
	 * 
	 * @return Access to the field data. Never null.
	 */
	public FieldIn<?> inFor(Field field);

}
