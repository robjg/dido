package dido.poi.layouts;

import dido.poi.columns.AbstractColumn;
import dido.poi.columns.BooleanColumn;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;

/**
 * @oddjob.description Define a column of Boolean cells.
 *
 * @author rob
 */
public class BooleanCell extends AbstractDataCell {

    private final BooleanColumn.Settings settings = BooleanColumn.with();

    @Override
    protected AbstractColumn.BaseSettings<?> settings() {
        return settings;
    }

    @Override
    public Type getType() {
        return BooleanColumn.TYPE;
    }

    @Override
    public CellType getCellType() {
        return CellType.BOOLEAN;
    }

}
