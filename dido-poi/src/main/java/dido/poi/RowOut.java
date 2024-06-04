package dido.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import dido.poi.style.StyleProvider;

/**
 * For writing cells.
 *
 * @author rob
 */
public interface RowOut extends StyleProvider {

    /**
     * Provide an outgoing cells representation for writing data to.
     *
     * @param columnIndex The column index.
     * @return A Cell. Never null.
     */
    Cell getCell(int columnIndex, CellType poiColumnType);
}
