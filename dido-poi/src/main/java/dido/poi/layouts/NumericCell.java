package dido.poi.layouts;

import dido.poi.columns.AbstractColumn;
import dido.poi.columns.NumericColumn;
import org.apache.poi.ss.usermodel.CellType;

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
    public Class<?> getType() {
        return settings.type();
    }

    /**
     * @oddjob.description The type of Number.
     * @oddjob.required No. Defaults to Double.
     */
    public void setType(Class<?> type) {
        this.settings.type(type);
    }

    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

}
