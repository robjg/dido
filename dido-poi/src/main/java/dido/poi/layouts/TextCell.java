package dido.poi.layouts;

import dido.poi.columns.AbstractColumn;
import dido.poi.columns.TextColumn;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @author rob
 * @oddjob.description Define a text column. Nests within a {@link DataRows}.
 */
public class TextCell extends AbstractDataCell {

    private final TextColumn.Settings settings = TextColumn.with();

    @Override
    protected AbstractColumn.BaseSettings<?> settings() {
        return settings;
    }

    @Override
    public Class<String> getType() {
        return TextColumn.TYPE;
    }

    @Override
    public CellType getCellType() {
        return CellType.STRING;
    }

}
