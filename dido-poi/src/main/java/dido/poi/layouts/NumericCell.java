package dido.poi.layouts;

import dido.data.DidoData;
import dido.how.DataException;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import dido.how.util.Primitives;
import dido.poi.CellIn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.function.Function;

/**
 * @oddjob.description Define a number column. Nests within a {@link DataRows}.
 *
 * @param <T>
 */
public class NumericCell<T extends Number> extends AbstractDataCell<T> {

    // TODO: Inject this.
    private DidoConversionProvider conversionProvider = DefaultConversionProvider.defaultInstance();

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
    public CellIn<T> provideCellIn(int index,
                                   DidoConversionProvider conversionProvider) {

        Class<T> type = getType();

        DoubleFunction<T> conversion = conversionProvider.fromDoubleTo(type);

        return rowIn -> {

            Cell cell = rowIn.getCell(index);

            try {
                return conversion.apply(cell.getNumericCellValue());
            }
            catch (RuntimeException e) {
                throw DataException.of(String.format("Failed to get number from cell [%d]:%s=%s"
                        ,getIndex(), getName(), cell.getStringCellValue()), e);
            }
        };
    }


    @Override
    void insertValueInto(Cell cell, int index, DidoData data) {

        Double use = this.value;
        if (use == null) {
            Object value = data.getAt(index);
            if (value != null) {
                if (Number.class.isAssignableFrom(value.getClass())) {
                    use = ((Number) value).doubleValue();
                } else {
                    Function<Object, Double> conversion = (Function<Object, Double>)
                            conversionProvider.conversionFor(value.getClass(), Double.class);
                    use = conversion.apply(value);
                }
            }
        }

        if (use == null) {
            cell.setBlank();
        }
        else {
            cell.setCellValue(use);
        }
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
