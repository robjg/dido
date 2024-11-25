package dido.poi;

import dido.how.conversion.DidoConversionProvider;

/**
 * Provide a way of reading a cell.
 *
 * @param <T> The type of data in the cell.
 */
public interface CellInProvider<T> {

    /**
     * Get the index of the cell. May be zero which should be interpreted
     * as the next column.
     *
     * @return the column index.
     */
    int getIndex();

    /**
     * Provide the reader of a cell at the given index. This will be the
     * value of {@link #getIndex()} or the new index if this was zero.
     *
     * @param columnIndex The column index. Always > 0.
     * @return The Cell In that provides the ability to read the data.
     */
    CellIn<T> provideCellIn(int columnIndex,
                            DidoConversionProvider conversionProvider);
}
