package dido.csv;

import dido.data.AbstractGenericData;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.SchemaBuilder;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.conversion.DefaultConverter;
import dido.how.conversion.DidoConverter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Objects;

public class CsvDataInHow implements DataInHow<String, InputStream> {


    private final CSVFormat csvFormat;

    private final DataSchema<String> schema;

    private final boolean withHeader;

    private final boolean partialSchema;

    private final DidoConverter converter;

    public static class Options {

        private CSVFormat csvFormat;

        private DataSchema<String> schema;

        private boolean withHeader;

        private boolean partialSchema;

        private DidoConverter converter;

        public Options csvFormat(CSVFormat csvFormat) {
            this.csvFormat = csvFormat;
            return this;
        }

        public Options schema(DataSchema<String> schema) {
            this.schema = schema;
            return this;
        }

        public Options withHeader(boolean withHeader) {
            this.withHeader = withHeader;
            return this;
        }

        public Options partialSchema(boolean partialSchema) {
            this.partialSchema = partialSchema;
            return this;
        }

        public Options converter(DidoConverter converter) {
            this.converter = converter;
            return this;
        }

        public DataInHow<String, InputStream> make() {
            return new CsvDataInHow(this);
        }
    }

    private CsvDataInHow(Options options) {
        this.csvFormat = Objects.requireNonNullElse(options.csvFormat, CSVFormat.DEFAULT);
        this.schema = options.schema;
        this.withHeader = options.withHeader;
        this.partialSchema = options.partialSchema;
        this.converter = Objects.requireNonNullElse(options.converter, new DefaultConverter());
    }

    public static Options withOptions() {
        return new Options();
    }

    public static DataInHow<String, InputStream> withDefaultOptions() {
        return new Options().make();
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

            if (this.withHeader || this.partialSchema) {
                if (iterator.hasNext()) {
                    schema = schemaFromHeader(iterator.next(), this.partialSchema ? this.schema : null);
                } else {
                    throw new IOException("No Header Record.");
                }
            } else {
                if (iterator.hasNext()) {
                    CSVRecord record = iterator.next();
                    schema = schemaNoHeader(record);
                    iterator = new OneAheadIterator<>(iterator, record);
                } else {
                    schema = DataSchema.emptySchema();
                }
            }
        } else {
            schema = this.schema;
            if (this.withHeader) {
                csvFormat = csvFormat.withFirstRecordAsHeader();
            }
            csvParser = csvFormat.parse(new InputStreamReader(inputStream));
            iterator = csvParser.iterator();
        }

        final Iterator<CSVRecord> finalIterator = iterator;

        return new DataIn<>() {

            @Override
            public GenericData<String> get() {
                if (finalIterator.hasNext()) {
                    return dataFrom(finalIterator.next(), schema, converter);
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

    static DataSchema<String> schemaNoHeader(CSVRecord record) {
        SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

        for (String ignored : record) {
            schemaBuilder.add(String.class);
        }
        return schemaBuilder.build();
    }

    static DataSchema<String> schemaFromHeader(CSVRecord record, DataSchema<String> partialSchema) {
        SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

        for (String field : record) {
            schemaBuilder.addField(field, String.class);
        }
        if (partialSchema != null) {
            schemaBuilder.merge(partialSchema);
        }
        return schemaBuilder.build();
    }

    static GenericData<String> dataFrom(CSVRecord record, DataSchema<String> schema, DidoConverter converter) {
        return new AbstractGenericData<>() {
            @Override
            public DataSchema<String> getSchema() {
                return schema;
            }

            @Override
            public Object getAt(int index) {
                return getAtAs(index, schema.getTypeAt(index));
            }

            @Override
            public <T> T getAtAs(int index, Class<T> type) {
                String value = record.get(index - 1);
                return converter.convertFromString(value, type);
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

        };
    }

    static class OneAheadIterator<E> implements Iterator<E> {

        private final Iterator<E> original;

        private E current;

        OneAheadIterator(Iterator<E> original, E current) {
            this.original = original;
            this.current = current;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            try {
                return current;
            } finally {
                if (original.hasNext()) {
                    current = original.next();
                } else {
                    current = null;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("CSV");
        if (this.schema != null) {
            if (partialSchema) {
                builder.append(", with partial schema");
            } else {
                builder.append(", with schema");
            }
        } else {
            builder.append(", with no schema");
        }
        if (withHeader) {
            builder.append(" and header");
        } else {
            builder.append(" and no header");
        }
        return builder.toString();
    }
}
