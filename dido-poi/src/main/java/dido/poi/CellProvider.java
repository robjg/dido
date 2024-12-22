package dido.poi;

/**
 * Something that provides cells.
 *
 * @see CellInProvider
 * @see CellOutProvider
 */
public interface CellProvider {

    /**
     * Get the index of the cell. May be zero which should be interpreted
     * as the next column.
     *
     * @return the column index.
     */
    int getIndex();

    Class<?> getType();

    String getName();


}
