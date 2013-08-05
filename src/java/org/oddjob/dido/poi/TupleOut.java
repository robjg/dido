package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.poi.style.StyleProvider;

/**
 * For writing a group of named values, generally a row.
 * 
 * @author rob
 *
 */
public interface TupleOut extends DataOut, StyleProvider {

	/**
	 * Provide the column index for the heading. The column index is
	 * one based.
	 * 
	 * @param heading The heading. May be null in which case the 
	 * next column will be returned.
	 * 
	 * @return The column index.
	 */
	public int indexForHeading(String heading);	
	
	/**
	 * Create a cell or get an existing one.
	 * 
	 * @param index The index for this cell in the tuple. The index is
	 * one based.
	 * @param type The Poi Type of the cell.
	 * 
	 * @return A Cell. Never null.
	 */
	public Cell createCell(int index, int type);
	
}
