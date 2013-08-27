package org.oddjob.dido.field;

/**
 * An abstraction for an item of data.
 * 
 * @author rob
 *
 */
public interface Field {

	/**
	 * Get the field label. The will be the heading for the a column in
	 * a CSV or spreadsheet for instance.
	 * 
	 * @return The label. May be null.
	 */
	public String getLabel();
	
}
