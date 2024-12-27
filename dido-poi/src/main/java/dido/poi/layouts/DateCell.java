package dido.poi.layouts;

import dido.poi.columns.AbstractColumn;
import dido.poi.columns.DateColumn;
import org.apache.poi.ss.usermodel.CellType;

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
    public Class<?> getType() {
        return settings.type();
    }

    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

    @Override
    public String getDefaultStyle() {
        return DEFAULT_DATE_STYLE;
    }

    public void setType(Class<?> type) {
        this.settings.type(type);
    }
}
