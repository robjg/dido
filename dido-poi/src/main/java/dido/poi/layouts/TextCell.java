package dido.poi.layouts;

import dido.data.GenericData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Optional;

/**
 * @author rob
 * @oddjob.description Read to and from a series of cells. Currently only
 * the ability to read columns of data by nesting within a {@link DataRows}
 * is supported but the ability to read rows will be added at some point.
 */
public class TextCell extends AbstractDataCell<String> {

    private volatile String value;

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public CellType getCellType() {
        return CellType.STRING;
    }

    @Override
    public String extractCellValue(Cell cell) {

        // We need this because even if the cell is a formatted cell
        // but contains a number we get a can't read from numeric cell
        // exception.
//		if (cell.getCellType() != CellType.STRING) {
//			cell.setCellType(CellType.STRING);
//		}
        return cell.getStringCellValue();
    }

    @Override
    void insertValueInto(Cell cell, int index, GenericData<String> data) {
        String value = Optional.ofNullable(this.value)
                .orElseGet(() -> data.getStringAt(index));

        cell.setCellValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
