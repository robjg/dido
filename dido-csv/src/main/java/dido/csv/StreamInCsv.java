package dido.csv;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;
import dido.data.SchemaBuilder;
import dido.pickles.DataIn;
import dido.pickles.StreamIn;
import dido.pickles.util.Primitives;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class StreamInCsv<F> implements StreamIn<F> {

    private static final Map<Class<?>, Function<String, Object>> CONVERSIONS = new HashMap<>();

    static {
        CONVERSIONS.put(Boolean.class, Boolean::valueOf);
        CONVERSIONS.put(Byte.class, Byte::valueOf);
        CONVERSIONS.put(Character.class, s -> s.isEmpty() ? null : s.charAt(0));
        CONVERSIONS.put(Short.class, Short::valueOf);
        CONVERSIONS.put(Integer.class, Integer::valueOf);
        CONVERSIONS.put(Long.class, Long::valueOf);
        CONVERSIONS.put(Float.class, Float::valueOf);
        CONVERSIONS.put(Double.class, Double::valueOf);
    }


    private final CSVFormat csvFormat;

    private final DataSchema<F> schema;

    private final boolean withHeaders;

    private final boolean partialSchema;

    public StreamInCsv(boolean withHeaders) {
        this(null, null, withHeaders, false);
    }

    public StreamInCsv(DataSchema<F> schema) {
        this(null, schema, false, false);
    }

    public StreamInCsv(DataSchema<F> schema, boolean withHeaders) {
        this(null, schema, withHeaders, false);
    }

    public StreamInCsv(DataSchema<F> schema, boolean withHeaders, boolean partialSchema) {
        this(null, schema, withHeaders, partialSchema);
    }

    public StreamInCsv(CSVFormat csvFormat, DataSchema<F> schema, boolean withHeaders, boolean partialSchema) {
        this.csvFormat = csvFormat == null ? CSVFormat.DEFAULT : csvFormat;
        this.schema = schema;
        this.withHeaders = withHeaders;
        this.partialSchema = partialSchema;
    }

    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn<F> inFrom(InputStream inputStream) throws IOException {

        CSVFormat csvFormat = this.csvFormat;

        // We don't know if the type is String or not.
        @SuppressWarnings("rawtypes") DataSchema schema;
        CSVParser csvParser;
        Iterator<CSVRecord> iterator;

        if (this.schema == null || this.partialSchema) {

                csvParser = csvFormat.parse(new InputStreamReader(inputStream));
                iterator = csvParser.iterator();

            if (iterator.hasNext()) {
                schema = schemaFromHeader(iterator.next(), this.partialSchema ? ((DataSchema<String>) this.schema) : null);
            }
            else {
                throw new IOException("No Header Record.");
            }
        } else {
            schema = this.schema;
            if (this.withHeaders) {
                csvFormat = csvFormat.withFirstRecordAsHeader();
            }
            csvParser = csvFormat.parse(new InputStreamReader(inputStream));
            iterator = csvParser.iterator();
        }

        return new DataIn<F>() {

            @Override
            public GenericData<F> get() {
                if (iterator.hasNext()) {
                    return dataFrom(iterator.next(), schema);
                } else {
                    return null;
                }
            }

            @Override
            public void close() throws IOException {
                csvParser.close();
            }
        };
    }

    public static DataSchema<String> schemaFromHeader(CSVRecord record, DataSchema<String> partialSchema) {
        SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

        for (String field : record) {
            schemaBuilder.addField(field, String.class);
        }
        if (partialSchema != null) {
            schemaBuilder.merge(partialSchema);
        }
        return schemaBuilder.build();
    }

    public static <F> GenericData<F> dataFrom(CSVRecord record, DataSchema<F> schema) {
        return new GenericData<F>() {
            @Override
            public DataSchema<F> getSchema() {
                return schema;
            }

            @Override
            public Object getAt(int index) {
                return record.get(index - 1);
            }

            @Override
            public <T> T getAtAs(int index, Class<T> type) {
                String value = record.get(index - 1);
                if (value == null) {
                    return null;
                }
                if (type.isAssignableFrom(String.class)) {
                    return type.cast(value);
                }

                type = Primitives.wrap(type);
                Function<String, ?> conversion = CONVERSIONS.get(type);
                if (conversion == null) {
                    throw new IllegalArgumentException("Unsupported CSV type " + type +
                            ", only Primitives and their Box types are supported.");
                }
                return type.cast(conversion.apply(value));
            }

            @Override
            public <T> T getAs(F field, Class<T> type) {
                int index = getSchema().getIndex(field);
                if (index > 0) {
                    return getAtAs(index, type);
                }
                else {
                    return null;
                }
            }

            @Override
            public boolean hasIndex(int index) {
                return record.get(index - 1) != null;
            }

            @Override
            public String getStringAt(int index) {
                return record.get(index - 1);
            }

            @Override
            public boolean getBooleanAt(int index) {
                return Boolean.getBoolean(getStringAt(index));
            }

            @Override
            public byte getByteAt(int index) {
                return Byte.parseByte(getStringAt(index));
            }

            @Override
            public char getCharAt(int index) {
                String s = getStringAt(index);
                return s.isEmpty() ? 0 : s.charAt(0);
            }

            @Override
            public short getShortAt(int index) {
                return Short.parseShort(getStringAt(index));
            }

            @Override
            public int getIntAt(int index) {
                return Integer.parseInt(getStringAt(index));
            }

            @Override
            public long getLongAt(int index) {
                return Long.parseLong(getStringAt(index));
            }

            @Override
            public float getFloatAt(int index) {
                return Float.parseFloat(getStringAt(index));
            }

            @Override
            public double getDoubleAt(int index) {
                return Double.parseDouble(getStringAt(index));
            }

            @Override
            public boolean getBoolean(F field) {
                return getAs(field, Boolean.class);
            }

            @Override
            public byte getByte(F field) {
                return getAs(field, Byte.class);
            }

            @Override
            public char getChar(F field) {
                return getAs(field, Character.class);
            }

            @Override
            public short getShort(F field) {
                return getAs(field, Short.class);
            }

            @Override
            public int getInt(F field) {
                return getAs(field, Integer.class);
            }

            @Override
            public long getLong(F field) {
                return getAs(field, Long.class);
            }

            @Override
            public float getFloat(F field) {
                return getAs(field, Float.class);
            }

            @Override
            public double getDouble(F field) {
                return getAs(field, Double.class);
            }

            @Override
            public String getString(F field) {
                return getAs(field, String.class);
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof IndexedData) {
                    return IndexedData.equals(this, (IndexedData<?>) o);
                }
                else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return IndexedData.hashCode(this);
            }

            @Override
            public String toString() {
                return IndexedData.toString(this);
            }
        };
    }

}
