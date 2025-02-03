package dido.poi.layouts;

import dido.poi.columns.AbstractColumn;
import dido.poi.columns.DateColumn;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;

/**
 * @author rob
 * @oddjob.description Define a date column. Nests within a {@link DataRows}.
 */
public class DateCell extends AbstractDataCell {

    public static final String DEFAULT_DATE_STYLE = "date";

    private final DateColumn.Settings settings = DateColumn.with();

    @Override
    protected AbstractColumn.BaseSettings<?> settings() {
        return settings;
    }

    @Override
    public Type getType() {
        return DateColumn.TYPE;
    }

    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

    @Override
    public String getDefaultStyle() {
        return DEFAULT_DATE_STYLE;
    }
}
