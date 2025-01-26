package dido.csv;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.useful.AbstractData;
import dido.data.useful.AbstractFieldGetter;
import dido.data.useful.DataSchemaImpl;
import dido.how.conversion.DidoConversionProvider;
import org.apache.commons.csv.CSVRecord;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Wraps a CsvRecord.
 */
public class CsvData extends AbstractData {

    private final Schema schema;

    private final CSVRecord record;

    private CsvData(Schema schema, CSVRecord record) {
        this.schema = schema;
        this.record = record;
    }

    @Override
    public ReadSchema getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        return schema.getters[index - 1].get(this);
    }

    @Override
    public boolean hasAt(int index) {
        return record.get(index - 1) != null;
    }

    @Override
    public String getStringAt(int index) {
        return record.get(index - 1);
    }

    @Override
    public boolean getBooleanAt(int index) {
        return schema.getters[index - 1].getBoolean(this);
    }

    @Override
    public byte getByteAt(int index) {

        return schema.getters[index - 1].getByte(this);
    }

    @Override
    public char getCharAt(int index) {
        return schema.getters[index - 1].getChar(this);
    }

    @Override
    public short getShortAt(int index) {

        return schema.getters[index - 1].getShort(this);
    }

    @Override
    public int getIntAt(int index) {

        return schema.getters[index - 1].getInt(this);
    }

    @Override
    public long getLongAt(int index) {

        return schema.getters[index - 1].getLong(this);
    }

    @Override
    public float getFloatAt(int index) {

        return schema.getters[index - 1].getFloat(this);
    }

    @Override
    public double getDoubleAt(int index) {

        return schema.getters[index - 1].getDouble(this);
    }

    static class StringGetter extends AbstractFieldGetter {

        private final int arrayIndex;

        StringGetter(int arrayIndex) {
            this.arrayIndex = arrayIndex;
        }

        @Override
        public Object get(DidoData data) {
            return ((CsvData) data).record.get(arrayIndex);
        }

        @Override
        public String getString(DidoData data) {
            return ((CsvData) data).record.get(arrayIndex);
        }
    }

    static class ConversionGetter<F> extends AbstractFieldGetter {

        private final int arrayIndex;

        private final Function<String, F> conversion;

        ConversionGetter(int arrayIndex, Function<String, F> conversion) {
            this.arrayIndex = arrayIndex;
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            String s = ((CsvData) data).record.get(arrayIndex);
            return conversion.apply(s);
        }

    }

    static class CharGetter extends AbstractFieldGetter {

        private final int arrayIndex;

        CharGetter(int arrayIndex) {
            this.arrayIndex = arrayIndex;
        }

        @Override
        public Object get(DidoData data) {
            return getChar(data);
        }

        @Override
        public char getChar(DidoData data) {
            String s = ((CsvData) data).record.get(arrayIndex);
            return s.isEmpty() ? 0 : s.charAt(0);
        }
    }

    static class IntGetter extends AbstractFieldGetter {

        private final int arrayIndex;

        private final ToIntFunction<String> conversion;

        IntGetter(int arrayIndex, ToIntFunction<String> conversion) {
            this.arrayIndex = arrayIndex;
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            return getInt(data);
        }

        @Override
        public int getInt(DidoData data) {
            String s = ((CsvData) data).record.get(arrayIndex);
            return s.isEmpty() ? 0 : conversion.applyAsInt(s);
        }
    }

    static class LongGetter extends AbstractFieldGetter {

        private final int arrayIndex;

        private final ToLongFunction<String> conversion;

        LongGetter(int arrayIndex, ToLongFunction<String> conversion) {
            this.arrayIndex = arrayIndex;
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            return getLong(data);
        }

        @Override
        public long getLong(DidoData data) {
            String s = ((CsvData) data).record.get(arrayIndex);
            return s.isEmpty() ? 0L : conversion.applyAsLong(s);
        }
    }

    static class DoubleGetter extends AbstractFieldGetter {

        private final int arrayIndex;

        private final ToDoubleFunction<String> conversion;

        DoubleGetter(int arrayIndex, ToDoubleFunction<String> conversion) {
            this.arrayIndex = arrayIndex;
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            return getDouble(data);
        }

        @Override
        public double getDouble(DidoData data) {
            String s = ((CsvData) data).record.get(arrayIndex);
            return s.isEmpty() ? 0.0 : conversion.applyAsDouble(s);
        }
    }

    public static Function<CSVRecord, CsvData> wrapperFunctionFor(DataSchema schema,
                                                                  DidoConversionProvider conversionProvider) {

        FieldGetter[] getters = new FieldGetter[schema.lastIndex()];

        for (SchemaField schemaField : schema.getSchemaFields()) {

            Class<?> type = schemaField.getType();
            int arrayIndex = schemaField.getIndex() - 1;
            if (type == String.class || type == Object.class) {
                getters[arrayIndex] = new StringGetter(arrayIndex);
            } else if (type == int.class) {
                getters[arrayIndex] = new IntGetter(arrayIndex,
                        conversionProvider.toIntFrom(String.class));
            } else if (type == long.class) {
                getters[arrayIndex] = new LongGetter(arrayIndex,
                        conversionProvider.toLongFrom(String.class));
            } else if (type == double.class) {
                getters[arrayIndex] = new DoubleGetter(arrayIndex,
                        conversionProvider.toDoubleFrom(String.class));
            } else if (type == char.class) {
                getters[arrayIndex] = new CharGetter(arrayIndex);
            } else {
                Function<String, ?> conversion = conversionProvider.conversionFor(String.class, type);
                getters[arrayIndex] = new ConversionGetter<>(arrayIndex, conversion);
            }
        }

        Schema csvSchema = schema instanceof DataSchemaImpl ?
                new Schema((DataSchemaImpl) schema, getters) :
                new Schema(schema.getSchemaFields(), schema.firstIndex(), schema.lastIndex(), getters);

        return record -> new CsvData(csvSchema, record);
    }


    static class Schema extends DataSchemaImpl implements ReadSchema {

        private final FieldGetter[] getters;

        Schema(Collection<SchemaField> fields,
               int firstIndex,
               int lastIndex,
               FieldGetter[] getters) {

            super(fields, firstIndex, lastIndex);
            this.getters = getters;
        }

        Schema(DataSchemaImpl schemaImpl,
               FieldGetter[] getters) {

            super(schemaImpl);
            this.getters = getters;
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            try {
                FieldGetter getter = getters[index - 1];
                if (getter == null) {
                    throw new NoSuchFieldException(index, Schema.this);
                }
                return getter;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, Schema.this);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, Schema.this);
            }
            return getters[index - 1];
        }
    }

}
