package dido.csv;

import dido.data.DataSchema;
import dido.how.DataInHow;
import dido.how.DataOutHow;
import dido.how.conversion.DidoConverter;
import org.apache.commons.csv.CSVFormat;

import java.io.InputStream;
import java.io.OutputStream;

public class CsvDido {

    private CSVFormat csvFormat;

    private DataSchema<String> schema;

    private boolean withHeader;

    private boolean partialSchema;

    /** Converter is only used for Input. Output is more complicated as the printer needs to know
     * if values should be quoted, so we can't pre convert to a String. */
    private DidoConverter converter;

    public DataOutHow<String, OutputStream> toStreamOut() {

        return CsvDataOutHow.withOptions()
                .csvFormat(csvFormat)
                .schema(schema)
                .withHeader(withHeader)
                .make();
    }

    public DataInHow<String, InputStream> toStreamIn() {

        return CsvDataInHow.withOptions()
                .csvFormat(csvFormat)
                .schema(schema)
                .withHeader(withHeader)
                .partialSchema(partialSchema)
                .converter(converter)
                .make();
    }

    public CSVFormat getCsvFormat() {
        return csvFormat;
    }

    public void setCsvFormat(CSVFormat csvFormat) {
        this.csvFormat = csvFormat;
    }

    public DataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(DataSchema<String> schema) {
        this.schema = schema;
    }

    public boolean isPartialSchema() {
        return partialSchema;
    }

    public void setPartialSchema(boolean partialSchema) {
        this.partialSchema = partialSchema;
    }

    public boolean isWithHeader() {
        return withHeader;
    }

    public void setWithHeader(boolean withHeader) {
        this.withHeader = withHeader;
    }

    public DidoConverter getConverter() {
        return converter;
    }

    public void setConverter(DidoConverter converter) {
        this.converter = converter;
    }
}
