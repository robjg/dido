package dido.poi.layouts;

import dido.data.DidoData;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Optional;

/**
 * @author rob
 * @oddjob.description Define a column of Boolean cells.
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
    public CellIn<Boolean> provideCellIn(int index,
                                         DidoConversionProvider conversionProvider) {

        return rowIn -> {

            Cell cell = rowIn.getCell(index);

            return cell.getBooleanCellValue();
        };
    }

    @Override
    void insertValueInto(Cell cell, int index, DidoData data) {

        Boolean value = Optional.ofNullable(this.value)
                .orElseGet(() -> (Boolean) data.getAt(index));

        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value);
        }
    }

}
