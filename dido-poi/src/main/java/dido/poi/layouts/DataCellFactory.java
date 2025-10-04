package dido.poi.layouts;

import dido.data.util.TypeUtil;
import dido.how.util.Primitives;
import dido.poi.CellProviderFactory;
import dido.poi.data.DataCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class DataCellFactory implements CellProviderFactory<DataCell> {

    /**
     * Taken from {@link org.apache.poi.ss.usermodel.BuiltinFormats}
     */
    private static final Set<Short> DATE_FORMATS = Set.of(
            (short) 0xe, // "m/ d/ yy"
            (short) 0xf, // "d-mmm-yy"
            (short) 0x10, // "d-mmm"
            (short) 0x11, // "mmm-yy"
            (short) 0x12, // "h:mm AM/ PM"
            (short) 0x13, // "h:mm:ss AM/ PM"
            (short) 0x14, // "h:mm"
            (short) 0x15, // "h:mm:ss",
            (short) 0x16 // "m/ d/ yy h:mm"
    );

    @Override
    public DataCell cellProviderFor(int index, String name, Type type) {
        Class<?> cellType = TypeUtil.classOf(Primitives.wrap(type));

        AbstractDataCell cell;

        if (Number.class.isAssignableFrom(cellType)) {
            cell = new NumericCell();
        } else if (Boolean.class == cellType) {
            cell = new BooleanCell();
        } else if (LocalDateTime.class.isAssignableFrom(cellType)) {
            cell = new DateCell();
        } else if (LocalDate.class.isAssignableFrom(cellType)) {
            cell = new DateCell();
        } else if (Date.class.isAssignableFrom(cellType)) {
            cell = new DateCell();
        } else {
            cell = new TextCell();
        }

        cell.setName(name);
        cell.setIndex(index);

        return  cell;
    }

    @Override
    public AbstractDataCell cellProviderFor(int index, String name, Cell cell) {

        AbstractDataCell dataCell;

        switch (cell.getCellType()) {
            case STRING:
            case ERROR:
                dataCell = new TextCell();
                break;
            case BOOLEAN:
                dataCell = new BooleanCell();
                break;
            case NUMERIC:
                CellStyle cellStyle = cell.getCellStyle();
                if (DATE_FORMATS.contains(cellStyle.getDataFormat())
                    || cellStyle.getDataFormatString().contains("yy")) {
                    dataCell = new DateCell();
                } else {
                    dataCell = new NumericCell();
                }
                break;
            case FORMULA:
                dataCell = new NumericFormulaCell();
                break;
            case BLANK:
            default:
                dataCell = new BlankCell();
                break;
        }

        dataCell.setIndex(index);
        dataCell.setName(name);

        return dataCell;
    }

}
