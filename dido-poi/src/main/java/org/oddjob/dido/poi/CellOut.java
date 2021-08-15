package org.oddjob.dido.poi;

import org.oddjob.dido.poi.data.CellLayout;
import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.ColumnOut;

/**
 * Represents a spreadsheet cell for writing data too. Implementations
 * must honour the methods for {@link CellLayout} if provided must also
 * work as with standard {@link Column} types.
 * 
 * @author rob
 *
 * @param <T>
 */
public interface CellOut<T> extends ColumnOut<T> {

	public String getCellReference();
}
