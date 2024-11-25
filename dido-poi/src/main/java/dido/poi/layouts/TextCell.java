package dido.poi.layouts;

import dido.data.DidoData;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Optional;

/**
 * @oddjob.description Define a text column. Nests within a {@link DataRows}.
 *
 * @author rob
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
    public CellIn<String> provideCellIn(int index,
                                        DidoConversionProvider conversionProvider) {

        return rowIn -> {

            Cell cell = rowIn.getCell(index);

            return cell.getStringCellValue();
        };
    }

    @Override
    void insertValueInto(Cell cell, int index, DidoData data) {
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
