package dido.csv;

import dido.data.DataSchema;
import org.apache.commons.csv.CSVFormat;
import org.oddjob.dido.StreamIn;
import org.oddjob.dido.StreamOut;

public class CsvDido {

    private CSVFormat csvFormat;

    private DataSchema<String> schema;

    private boolean withHeaders;

    private boolean partialSchema;

    public StreamOut<String> toStreamOut() {

        return new StreamOutCsv(csvFormat, schema, withHeaders);
    }

    public StreamIn<String> toStreamIn() {

        return new StreamInCsv(csvFormat, schema, withHeaders, partialSchema);
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

    public boolean isWithHeaders() {
        return withHeaders;
    }

    public void setWithHeaders(boolean withHeaders) {
        this.withHeaders = withHeaders;
    }
}
