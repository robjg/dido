package org.oddjob.dido.poi;

import org.apache.poi.hssf.record.formula.functions.Column;
import org.oddjob.dido.poi.data.CellLayout;
import org.oddjob.dido.tabular.ColumnIn;

/**
 * Represents a spreadsheet cell for read data from. Implementations
 * must honour the methods for {@link CellLayout} if provided must also
 * work as with standard {@link Column} types.
 * 
 * @author rob
 *
 * @param <T>
 */
public interface CellIn<T> extends ColumnIn<T> {

	public String getCellReference();
}
