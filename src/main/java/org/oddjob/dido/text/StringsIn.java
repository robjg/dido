package org.oddjob.dido.text;

import org.oddjob.dido.DataIn;

/**
 * A group of text items for reading.
 * <p>
 * At the moment this is supported by {@link SimpleTextFieldsIn} to
 * enable {@link NamedValuesLayout} to read data from a 
 * {@link DelimitedLayout}. It is intended that a number of lines could
 * also be of this type to support parsing things like properties files.
 * 
 * @author rob
 *
 */
public interface StringsIn extends DataIn {

	/**
	 * Get the string items.
	 * 
	 * @return The string items. Must not be null.
	 */
	public String[] getValues();
}
