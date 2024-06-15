package dido.poi.layouts;

import dido.data.DidoData;
import dido.how.util.Primitives;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Optional;

/**
 * @oddjob.description Define a number column. Nests within a {@link DataRows}.
 *
 * @param <T>
 */
public class NumericCell<T extends Number> extends AbstractDataCell<T> {

    /**
     * @oddjob.description The type of Number.
     * @oddjob.required No. Defaults to Double.
     */
    private volatile Class<T> type;

    private volatile Double value;

    @Override
    public Class<T> getType() {
        //noinspection unchecked
        return Optional.ofNullable(type).orElse((Class<T>) Double.class);
    }

    public void setType(Class<T> type) {
        if (type != null && type.isPrimitive()) {
            this.type = Primitives.wrap(type);
        } else {
            this.type = type;
        }
    }

    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

    @Override
    public T extractCellValue(Cell cell) {
        Class<T> type = getType();

        double value = cell.getNumericCellValue();
        if (type == Double.class) {
            //noinspection unchecked
            return (T) Double.valueOf(value);
        }
        if (type == Integer.class) {
            //noinspection unchecked
            return (T) Integer.valueOf((int) value);
        }
        if (type == Long.class) {
            //noinspection unchecked
            return (T) Long.valueOf((long) value);
        }
        if (type == Float.class) {
            //noinspection unchecked
            return (T) Float.valueOf((float) value);
        }
        if (type == Short.class) {
            //noinspection unchecked
            return (T) Short.valueOf((short) value);
        }
        if (type == Byte.class) {
            //noinspection unchecked
            return (T) Byte.valueOf((byte) value);
        }
        throw new IllegalArgumentException("Not Numeric type " + type);
    }

    @Override
    void insertValueInto(Cell cell, int index, DidoData data) {

        Number value = Optional.<Number>ofNullable(this.value)
                .orElseGet(() -> data.getAtAs(index, Number.class));

        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value.doubleValue());
        }
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
