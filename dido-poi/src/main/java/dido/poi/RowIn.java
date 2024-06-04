package dido.poi;

import org.apache.poi.ss.usermodel.Cell;

/**
 * For reading a  row.
 * 
 * @author rob
 *
 */
public interface RowIn {
		
	/**
	 * Provide cell for reading data from.
	 * 
	 * @param columnIndex The column index.
	 * 
	 * @return A Cell. Never null.
	 */
	Cell getCell(int columnIndex);
}
