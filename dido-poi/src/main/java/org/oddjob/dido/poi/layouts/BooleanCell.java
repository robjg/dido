package org.oddjob.dido.poi.layouts;

import dido.data.GenericData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Optional;

/**
 * @author rob
 * @oddjob.description Create a column of boolean cells.
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
    void insertValueInto(Cell cell, int index, GenericData<String> data) {

        Boolean value = Optional.ofNullable(this.value)
                .orElseGet(() -> data.getAtAs(index, Boolean.class));

        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value);
        }
    }

}
