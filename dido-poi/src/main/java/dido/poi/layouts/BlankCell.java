package dido.poi.layouts;

import dido.data.DidoData;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @author rob
 * @oddjob.description Create a column cells that are blank. Provided for
 * completeness - not sure of the use case for this.
 */
public class BlankCell extends AbstractDataCell<Void> {

    @Override
    public Class<Void> getType() {
        return Void.class;
    }

    @Override
    public CellType getCellType() {
        return CellType.BLANK;
    }

    @Override
    public CellIn<Void> provideCellIn(int index,
                                      DidoConversionProvider conversionProvider) {

        return rowIn -> null;
    }

    @Override
    void insertValueInto(Cell cell, int index, DidoData data) {
        cell.setBlank();
    }
}
