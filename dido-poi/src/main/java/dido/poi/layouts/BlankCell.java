package dido.poi.layouts;

import dido.data.GenericData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @oddjob.description Create a column cells that are blank. Provided for
 * completeness - not sure of the use case for this.
 *
 * @author rob
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
    public Void extractCellValue(Cell cell) {
        return null;
    }

    @Override
    void insertValueInto(Cell cell, int index, GenericData<String> data) {
        cell.setBlank();
    }
}
