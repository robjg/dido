package dido.poi;

import dido.data.DataSchema;
import dido.how.conversion.DidoConversionProvider;

/**
 * Provide a way of reading a cell.
 *
 */
public interface CellInProvider extends CellProvider {

    /**
     * Provide the reader of a cell at the given index. This will be the
     * value of {@link #getIndex()} or the new index if this was zero.
     *
     * @param columnIndex The column index. Always &gt; 0.
     * @return The Cell In that provides the ability to read the data.
     */
    CellIn provideCellIn(int columnIndex,
                         DataSchema schema,
                         DidoConversionProvider conversionProvider);
}
