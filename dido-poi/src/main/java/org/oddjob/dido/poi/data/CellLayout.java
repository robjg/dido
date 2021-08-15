package org.oddjob.dido.poi.data;

import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.dido.DataException;
import org.oddjob.dido.ValueNode;

public interface CellLayout<T> extends ValueNode<T> {

	/**
	 * Extract the value from the cell.
	 * 
	 * @param cell
	 * @throws DataException
	 */
	public T extractCellValue(Cell cell)
	throws DataException;
	
	/**
	 * Write a value into the cell.
	 * 
	 * @param cell
	 * @throws DataException
	 */
	public void insertValueInto(Cell cell, T value)
	throws DataException;
	
	/**
	 */
	public int getCellType();

	/**
	 * Provide the style name for the cell.
	 * 
	 * @return
	 */
	public String getStyle();

	
	public String getDefaultStyle();
}
