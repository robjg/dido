package org.oddjob.dido.text;

import org.oddjob.dido.tabular.Column;

/**
 * Representation of a column of text in a fixed width layout.
 * 
 * @see FixedWidthTextFieldsIn
 * @see FixedWidthTextFieldsOut
 * 
 * @author rob
 *
 */
public interface FixedWidthColumn extends Column {

	/**
	 * The length of the text.
	 * 
	 * @return
	 */
	public int getLength();
}
