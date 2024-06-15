package dido.poi.layouts;

import dido.data.DidoData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Optional;

/**
 * @oddjob.description Define a column of Boolean cells.
 *
 * @author rob
 */
public class BooleanCell extends AbstractDataCell<Boolean> {

    private volatile Boolean value;

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public CellType getCellType() {
        return CellType.BOOLEAN;
    }

    @Override
    public Boolean extractCellValue(Cell cell) {
        return cell.getBooleanCellValue();
    }

    @Override
    void insertValueInto(Cell cell, int index, DidoData data) {

        Boolean value = Optional.ofNullable(this.value)
                .orElseGet(() -> data.getAtAs(index, Boolean.class));

        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value);
        }
    }

}
