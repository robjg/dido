package dido.poi.layouts;

import dido.data.DataSchema;
import dido.data.ReadSchema;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import dido.poi.CellOut;
import dido.poi.columns.AbstractColumn;
import dido.poi.columns.BooleanColumn;
import org.apache.poi.ss.usermodel.CellType;

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
    public Class<Boolean> getType() {
        return BooleanColumn.TYPE;
    }

    @Override
    public CellType getCellType() {
        return CellType.BOOLEAN;
    }

    @Override
    public CellIn provideCellIn(int columnIndex, DataSchema schema, DidoConversionProvider conversionProvider) {
        return BooleanColumn.with()
                .name(getName())
                .index(getIndex())
                .style(getStyle())
                .make()
                .provideCellIn(columnIndex, schema, conversionProvider);
    }

    @Override
    public CellOut provideCellOut(ReadSchema schema, int index, DidoConversionProvider conversionProvider) {
        return BooleanColumn.with()
                .name(getName())
                .index(getIndex())
                .style(getStyle())
                .make()
                .provideCellOut(schema, index, conversionProvider);
    }
}
