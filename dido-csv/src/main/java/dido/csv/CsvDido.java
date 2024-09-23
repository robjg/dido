package dido.csv;

import dido.data.DataSchema;
import dido.data.NamedData;
import dido.how.DataInHow;
import dido.how.DataOutHow;
import dido.how.conversion.DidoConversionProvider;
import org.apache.commons.csv.CSVFormat;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @oddjob.description Creates an In or Out for CSV Data.
 *
 * @oddjob.example From CSV data and back again.
 * {@oddjob.xml.resource dido/csv/FromToCsvExample.xml}
 *
 */
public class CsvDido {

    /**
     * @oddjob.description The CSV Format to use. See the <a href="https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html">CSVFormat JavaDoc</a>
     * for more information.
     * @oddjob.required No.
     */
    private CSVFormat csvFormat;

    /**
     * @oddjob.description The schema to use. When reading in, if one is not provided an all String schema will be
     * created. When writing out the schema is only used to provide a header in the event that no data arrives. It
     * is expected to match the schema of the data. No check is made to ensure it does.
     * @oddjob.required No.
     */
    private DataSchema schema;

    /**
     * @oddjob.description Does the data contain a header or is a header to be written.
     * @oddjob.required No, defaults to false.
     */
    private boolean withHeader;

    /**
     * @oddjob.description When reading data in, indicates that the provided Schema is partial. The
     * rest of the schema will be taken from the header.
     * @oddjob.required No, defaults to false.
     */
    private boolean partialSchema;

    /** @oddjob.description A converter used to convert Strings to the required schema type.
     * Note Converter is only used for Input. Output is more complicated as the printer needs to know
     * if values should be quoted, so we can't pre convert to a String.
     * @oddjob.required No
     */
    private DidoConversionProvider converter;

    public DataOutHow<OutputStream> toStreamOut() {

        return CsvDataOutHow.withOptions()
                .csvFormat(csvFormat)
                .schema(schema)
                .withHeader(withHeader)
                .make();
    }

    public DataInHow<InputStream, NamedData> toStreamIn() {

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

    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
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

    public DidoConversionProvider getConverter() {
        return converter;
    }

    public void setConverter(DidoConversionProvider converter) {
        this.converter = converter;
    }
}
