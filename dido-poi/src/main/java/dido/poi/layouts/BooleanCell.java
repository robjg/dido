package dido.poi.layouts;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import dido.how.util.Primitives;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.function.Function;

/**
 * @author rob
 * @oddjob.description Define a column of Boolean cells.
 */
public class BooleanCell extends AbstractDataCell {

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
    protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {

        Class<?> type = Primitives.wrap(schemaField.getType());

        if (type.isAssignableFrom(Boolean.class)) {
            return new BooleanCellGetter(schemaField);
        } else {
            return new BooleanCellGetterWithConversion<>(schemaField,
                    RequiringConversion.with(conversionProvider).from(Boolean.class).to(type));
        }
    }

    static class BooleanCellGetter extends AbstractCellGetter {

        BooleanCellGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getBoolean(data);
        }

        @Override
        public boolean getBoolean(DidoData data) {
            return getCell(data).getBooleanCellValue();
        }
    }

    static class BooleanCellGetterWithConversion<R> extends BooleanCellGetter {

        private final Function<Boolean, R> conversion;

        BooleanCellGetterWithConversion(SchemaField schemaField,
                                        Function<Boolean, R> conversion) {
            super(schemaField);
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            try {
                return conversion.apply(super.getBoolean(data));
            } catch (RuntimeException e) {
                throw DataException.of(String.format("Failed to get value for %s", this), e);
            }
        }
    }

    @Override
    protected Injector injectorFor(SchemaField schemaField, FieldGetter getter, DidoConversionProvider conversionProvider) {

        Class<?> fromType = schemaField.getType();

        Function<Object, Boolean> conversion;

        if (Boolean.class.isAssignableFrom(fromType)) {
            conversion = null;
        } else {
            //noinspection unchecked
            conversion = (Function<Object, Boolean>) conversionProvider.conversionFor(fromType, Boolean.class);
        }

        return new Injector() {
            @Override
            public void insertValueInto(Cell cell, DidoData data) {

                if (!getter.has(data)) {
                    cell.setBlank();
                    return;
                }

                boolean value;
                if (conversion == null) {
                    value = (getter.getBoolean(data));
                } else {
                    value = conversion.apply(getter.get(data));
                }

                cell.setCellValue(value);
            }
        };
    }
}
