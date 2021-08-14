package dido.csv;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.SchemaBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.oddjob.dido.CloseableSupplier;
import org.oddjob.dido.StreamIn;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class StreamInCsv<F> implements StreamIn<F> {

    private final CSVFormat csvFormat;

    private final DataSchema<F> schema;

    private final boolean withHeaders;

    private final boolean partialSchema;

    public StreamInCsv(boolean withHeaders) {
        this(null, null, withHeaders, false);
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
    public CloseableSupplier<GenericData<F>> supplierFor(InputStream inputStream) throws IOException {

        CSVFormat csvFormat = this.csvFormat;

        // We don't know if the type is String or not.
        DataSchema schema;
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
        }
        else {
            schema = this.schema;
            if (this.withHeaders) {
                csvFormat = csvFormat.withFirstRecordAsHeader();
            }
            csvParser = csvFormat.parse(new InputStreamReader(inputStream));
            iterator = csvParser.iterator();
        }

        return new CloseableSupplier<GenericData<F>>() {
            @Override
            public void close() throws IOException {
                csvParser.close();
            }

            @Override
            public GenericData<F> get() {
                if (iterator.hasNext()) {
                    return dataFrom(iterator.next(), schema);
                }
                else {
                    return null;
                }
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
            public <T> T getObjectAt(int index, Class<T> type) {
                String value = record.get(index - 1);
                if (value == null) {
                    return null;
                }
                if (type.isAssignableFrom(String.class)) {
                    return type.cast(value);
                }
                try {
                    Method method = type.getMethod("valueOf", String.class);
                    return (T) method.invoke(null, value);
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException(e);
                } catch (InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public boolean hasIndex(int index) {
                return record.get(index) != null;
            }
        };
    }

}
