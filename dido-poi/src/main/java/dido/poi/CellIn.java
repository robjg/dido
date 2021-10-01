package dido.poi;

import org.oddjob.dido.poi.RowIn;

/**
 * Represents a spreadsheet cell for reading data from.
 * 
 * @author rob
 *
 * @param <T>
 */
public interface CellIn<T> {

	/**
	 * Extract the value from the cell.
	 *
	 * @param rowIn The row
	 * @return The extracted value. May be null.
	 */
	T getValue(RowIn rowIn);
}
