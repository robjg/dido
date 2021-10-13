package dido.csv;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;
import dido.data.SchemaBuilder;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.util.Primitives;
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

public class StreamInCsv implements DataInHow<String, InputStream> {

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
        CONVERSIONS.put(Number.class, Double::valueOf);
    }


    private final CSVFormat csvFormat;

    private final DataSchema<String> schema;

    private final boolean withHeaders;

    private final boolean partialSchema;

    public StreamInCsv(boolean withHeaders) {
        this(null, null, withHeaders, false);
    }

    public StreamInCsv(DataSchema<String> schema) {
        this(null, schema, false, false);
    }

    public StreamInCsv(DataSchema<String> schema, boolean withHeaders) {
        this(null, schema, withHeaders, false);
    }

    public StreamInCsv(DataSchema<String> schema, boolean withHeaders, boolean partialSchema) {
        this(null, schema, withHeaders, partialSchema);
    }

    public StreamInCsv(CSVFormat csvFormat, DataSchema<String> schema, boolean withHeaders, boolean partialSchema) {
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
    public DataIn<String> inFrom(InputStream inputStream) throws IOException {

        CSVFormat csvFormat = this.csvFormat;

        DataSchema<String> schema;
        CSVParser csvParser;
        Iterator<CSVRecord> iterator;

        if (this.schema == null || this.partialSchema) {

                csvParser = csvFormat.parse(new InputStreamReader(inputStream));
                iterator = csvParser.iterator();

            if (iterator.hasNext()) {
                schema = schemaFromHeader(iterator.next(), this.partialSchema ? this.schema : null);
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

        return new DataIn<>() {

            @Override
            public GenericData<String> get() {
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

    public static GenericData<String> dataFrom(CSVRecord record, DataSchema<String> schema) {
        return new GenericData<>() {
            @Override
            public DataSchema<String> getSchema() {
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
            public <T> T getAs(String field, Class<T> type) {
                int index = getSchema().getIndex(field);
                if (index > 0) {
                    return getAtAs(index, type);
                } else {
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
            public boolean getBoolean(String field) {
                return getBooleanAt(schema.getIndex(field));
            }

            @Override
            public byte getByte(String field) {
                return getByteAt(schema.getIndex(field));
            }

            @Override
            public char getChar(String field) {
                return getCharAt(schema.getIndex(field));
            }

            @Override
            public short getShort(String field) {
                return getShortAt(schema.getIndex(field));
            }

            @Override
            public int getInt(String field) {
                return getIntAt(schema.getIndex(field));
            }

            @Override
            public long getLong(String field) {
                return getLongAt(schema.getIndex(field));
            }

            @Override
            public float getFloat(String field) {
                return getFloatAt(schema.getIndex(field));
            }

            @Override
            public double getDouble(String field) {
                return getDoubleAt(schema.getIndex(field));
            }

            @Override
            public String getString(String field) {
                return getStringAt(schema.getIndex(field));
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof IndexedData) {
                    return IndexedData.equals(this, (IndexedData<?>) o);
                } else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return IndexedData.hashCode(this);
            }

            @Override
            public String toString() {
                return GenericData.toString(this);
            }
        };
    }

}
