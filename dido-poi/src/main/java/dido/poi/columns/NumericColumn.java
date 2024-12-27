package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import dido.how.util.Primitives;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.Function;

/**
 * Define a number column.
 */
public class NumericColumn extends AbstractColumn {

    /**
     * The type of Number. Defaults to Double.
     */
    private final Class<?> type;

    protected NumericColumn(Settings settings) {
        super(settings);
        this.type = settings.type();
    }

    public static class Settings extends AbstractColumn.BaseSettings<Settings> {

        private Class<?> type;

        @Override
        protected Settings self() {
            return this;
        }

        public Settings type(Class<?> type) {
            if (type == null) {
                this.type = null;
            } else {
                type = Primitives.wrap(type);
                if (Number.class.isAssignableFrom(type)) {
                    this.type = type;
                } else {
                    throw new IllegalArgumentException("Type must be a Number");
                }
            }
            return this;
        }

        public Class<?> type() {
            return Objects.requireNonNullElse(this.type, Double.class);
        }

        public NumericColumn make() {
            return new NumericColumn(this);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<?> getType() {
        return Objects.requireNonNullElse(type, Double.class);
    }


    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

    @Override
    protected FieldGetter getterFor(SchemaField schemaField,
                                    DidoConversionProvider conversionProvider) {

        Class<?> type = Primitives.wrap(schemaField.getType());
        if (type.isAssignableFrom(Double.class)) {
            return new DoubleGetter(schemaField);
        } else if (type.isAssignableFrom(Integer.class)) {
            return new IntGetter(schemaField);
        } else if (type.isAssignableFrom(Long.class)) {
            return new LongGetter(schemaField);
        } else if (type.isAssignableFrom(Short.class)) {
            return new ShortGetter(schemaField);
        } else if (type.isAssignableFrom(Byte.class)) {
            return new ByteGetter(schemaField);
        } else if (type.isAssignableFrom(Float.class)) {
            return new FloatGetter(schemaField);
        } else {
            return new DoubleCellGetterWithConversion<>(schemaField,
                    RequiringConversion.with(conversionProvider).fromDoubleTo(type));
        }
    }

    abstract static class NumberGetter extends AbstractCellGetter {

        NumberGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public byte getByte(DidoData data) {
            return (byte) getDouble(data);
        }

        @Override
        public short getShort(DidoData data) {
            return (short) getDouble(data);
        }

        @Override
        public int getInt(DidoData data) {
            return (int) getDouble(data);
        }

        @Override
        public long getLong(DidoData data) {
            return (long) getDouble(data);
        }

        @Override
        public float getFloat(DidoData data) {
            return (float) getDouble(data);
        }

        @Override
        public double getDouble(DidoData data) {
            return getCell(data).getNumericCellValue();
        }
    }

    static class ByteGetter extends NumberGetter {

        ByteGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getByte(data);
        }
    }

    static class ShortGetter extends NumberGetter {

        ShortGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getShort(data);
        }
    }

    static class IntGetter extends NumberGetter {

        IntGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getInt(data);
        }
    }

    static class LongGetter extends NumberGetter {

        LongGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getLong(data);
        }
    }

    static class FloatGetter extends NumberGetter {

        FloatGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getFloat(data);
        }
    }

    static class DoubleGetter extends NumberGetter {

        DoubleGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getDouble(data);
        }
    }

    static class DoubleCellGetterWithConversion<R> extends NumberGetter {

        private final DoubleFunction<R> conversion;

        DoubleCellGetterWithConversion(SchemaField schemaField,
                                       DoubleFunction<R> conversion) {
            super(schemaField);
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            try {
                return conversion.apply(super.getDouble(data));
            } catch (RuntimeException e) {
                throw DataException.of(String.format("Failed to get value for %s", this), e);
            }
        }
    }

    @Override
    protected Injector injectorFor(SchemaField schemaField,
                                   FieldGetter getter,
                                   DidoConversionProvider conversionProvider) {

        Class<?> fromType = schemaField.getType();
        Class<?> toType = getType();

        Function<Object, Number> conversion;

        if (toType.isAssignableFrom(fromType)) {
            conversion = null;
        } else {
            //noinspection unchecked
            conversion = (Function<Object, Number>) conversionProvider.conversionFor(fromType, toType);
            if (conversion == null) {
                throw DataException.of("No Conversion from " + fromType + " to " + toType +
                        " in field " + schemaField);
            }
        }

        return new Injector() {

            @Override
            public void insertValueInto(Cell cell, DidoData data) {

                if (!getter.has(data)) {
                    cell.setBlank();
                    return;
                }

                double value;

                if (conversion != null) {
                    value = (conversion.apply(getter.get(data))).doubleValue();
                } else if (toType == Double.class) {
                    value = getter.getDouble(data);
                } else if (toType == Integer.class) {
                    value = getter.getInt(data);
                } else if (toType == Long.class) {
                    value = getter.getLong(data);
                } else if (toType == Byte.class) {
                    value = getter.getByte(data);
                } else if (toType == Short.class) {
                    value = getter.getShort(data);
                } else if (toType == Float.class) {
                    value = getter.getFloat(data);
                } else {
                    throw new IllegalStateException("Unexpected unsupported type " + toType);
                }

                cell.setCellValue(value);
            }
        };
    }

}
