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

    private boolean withHeadings;

    private boolean partialSchema;

    public DataOutHow<String, OutputStream> toStreamOut() {

        return new StreamOutCsv(csvFormat, schema, withHeadings);
    }

    public DataInHow<String, InputStream> toStreamIn() {

        return new StreamInCsv(csvFormat, schema, withHeadings, partialSchema);
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

    public boolean isWithHeadings() {
        return withHeadings;
    }

    public void setWithHeadings(boolean withHeadings) {
        this.withHeadings = withHeadings;
    }
}
