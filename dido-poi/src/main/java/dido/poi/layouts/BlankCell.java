package dido.poi.layouts;

import dido.poi.columns.AbstractColumn;
import dido.poi.columns.BlankColumn;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @oddjob.description Create a column cells that are blank. Provided for
 * completeness - not sure of the use case for this.
 *
 * @author rob
 */
public class BlankCell extends AbstractDataCell {

    private final BlankColumn.Settings settings = BlankColumn.with();

    @Override
    protected AbstractColumn.BaseSettings<?> settings() {
        return settings;
    }

    @Override
    public Class<Void> getType() {
        return BlankColumn.TYPE;
    }

    @Override
    public CellType getCellType() {
        return CellType.BLANK;
    }

}
