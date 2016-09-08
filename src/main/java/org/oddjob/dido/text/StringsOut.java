package org.oddjob.dido.text;

import org.oddjob.dido.DataOut;

/**
 * A group of text items to be written.
 * <p>
 * At the moment this is supported by {@link SimpleTextFieldsOut} to
 * enable {@link NamedValuesLayout} to write data to a 
 * {@link DelimitedLayout}. It is intended that a number of lines could
 * also be of this type to support writing things like properties files.
 * 
 * @author rob
 *
 */
public interface StringsOut extends DataOut {

	/**
	 * Set the string items.
	 * 
	 * @param values The items. May be empty, and may contain null elements
	 * but must itself be null.
	 */
	public void setValues(String[] values);
}
