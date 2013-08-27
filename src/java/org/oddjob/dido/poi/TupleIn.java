package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.ColumnIn;
import org.oddjob.dido.tabular.TabularDataIn;

/**
 * For reading a group of named values, generally a row.
 * 
 * @author rob
 *
 */
public interface TupleIn extends TabularDataIn {
	
	/**
	 * Provide the column index for the heading. The column index is
	 * one based.
	 * 
	 * @param heading The heading. May be null in which case the 
	 * next column will be returned.
	 * 
	 * @return The column index.
	 */
	public int indexForHeading(String title);
	
	/**
	 * Get a cell.
	 * 
	 * @param index The index for this cell in the tuple. The index is
	 * one based.
	 * 
	 * @return A Cell. Could be null.
	 */
	public Cell getCell(int column);
		
	
	@Override
	public CellIn<?> inFor(Field column);
	
}
