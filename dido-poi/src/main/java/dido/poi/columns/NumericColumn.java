package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Define a number column.
 */
public class NumericColumn extends AbstractColumn {

    public static final Type TYPE = double.class;

    protected NumericColumn(Settings settings) {
        super(settings);
    }

    public static class Settings extends AbstractColumn.BaseSettings<Settings> {

        @Override
        protected Settings self() {
            return this;
        }

        public NumericColumn make() {
            return new NumericColumn(this);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Type getType() {
        return TYPE;
    }


    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

    @Override
    protected FieldGetter getterFor(SchemaField schemaField,
                                    DidoConversionProvider conversionProvider) {

        Type type = schemaField.getType();
        if (type == double.class) {
            return new DoubleGetter(schemaField);
        } else if (type == int.class) {
            return new IntGetter(schemaField);
        } else if (type == long.class) {
            return new LongGetter(schemaField);
        } else if (type == short.class) {
            return new ShortGetter(schemaField);
        } else if (type == byte.class) {
            return new ByteGetter(schemaField);
        } else if (type == float.class) {
            return new FloatGetter(schemaField);
        } else {
            return new DoubleCellGetterWithConversion<>(schemaField,
                    RequiringConversion.with(conversionProvider).
                            <Double>from(Double.class)
                            .to(type));
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

        private final Function<Double, R> conversion;

        DoubleCellGetterWithConversion(SchemaField schemaField,
                                       Function<Double, R> conversion) {
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

        Type fromType = schemaField.getType();

        Function<Object, Double> conversion;

        if (fromType == double.class) {
            conversion = null;
        } else {
            conversion = RequiringConversion.with(conversionProvider)
                    .from(fromType)
                    .to(Double.class);
        }

        return (cell, data) -> {

            if (!getter.has(data)) {
                cell.setBlank();
                return;
            }

            double value;

            if (conversion == null) {
                value = getter.getDouble(data);
            }
            else {
                value = (conversion.apply(getter.get(data)));
            }

            cell.setCellValue(value);
        };
    }

}
