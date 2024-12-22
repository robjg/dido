package dido.poi;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Something that creates cells for Reading or writing or both.
 *
 * @param <P> The type of Provider.
 */
public interface CellProviderFactory<P extends CellProvider> {

    P cellProviderFor(int index, String name, Class<?> type);

    P cellProviderFor(int index, String name, Cell cell);
}
