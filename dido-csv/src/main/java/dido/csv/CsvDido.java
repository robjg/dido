package dido.csv;

import dido.data.DataSchema;
import dido.how.DataInHow;
import dido.how.DataOutHow;
import org.apache.commons.csv.CSVFormat;

import java.io.InputStream;
import java.io.OutputStream;

public class CsvDido {

    private CSVFormat csvFormat;

    private DataSchema<String> schema;

    private boolean withHeader;

    private boolean partialSchema;

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
}
