package dido.poi.layouts;

import dido.poi.columns.AbstractColumn;
import dido.poi.columns.NumericColumn;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;

/**
 * @oddjob.description Define a number column. Nests within a {@link DataRows}.
 */
public class NumericCell extends AbstractDataCell {

    private final NumericColumn.Settings settings = NumericColumn.with();

    @Override
    protected AbstractColumn.BaseSettings<?> settings() {
        return settings;
    }

    @Override
    public Type getType() {
        return NumericColumn.TYPE;
    }

    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

}
