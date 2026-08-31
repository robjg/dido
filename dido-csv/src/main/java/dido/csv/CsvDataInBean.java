package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataInHow;
import dido.how.StreamHows;

import java.io.InputStream;
import java.util.function.Function;

/**
 * @oddjob.description Reads CSV format data to Dido Data.
 * <a href="https://commons.apache.org/proper/commons-csv/">Apache Commons CSV</a>
 * is used for this using the DEFAULT format. Please see their documentation
 * for more details.
 *
 * @oddjob.example From CSV data and back again.
 * {@oddjob.xml.resource dido/csv/FromToCsvExample.xml}
 *
 * @oddjob.example Using a Custom Format.
 * {@oddjob.xml.resource dido/csv/CustomFormatExample.xml}
 *
 */
public class CsvDataInBean extends CsvBeanBase {

    /**
     * @oddjob.description The schema to use. When reading in, if one is not provided an all String schema will be
     * created. When writing out the schema is only used to provide a header in the event that no data arrives. It
     * is expected to match the schema of the data. No check is made to ensure it does.
     * @oddjob.required No.
     */
    private DataSchema schema;

    /**
     * @oddjob.description When reading data in, indicates that the provided Schema is partial. The
     * rest of the schema will be taken from the header.
     * @oddjob.required No, defaults to false.
     */
    private boolean partialSchema;

    public DataInHow<InputStream> toStreamIn() {

        DataInCsv dataInCsv = DataInCsv.with()
                .csvFormat(getCsvFormat())
                .schema(schema)
                .header(isWithHeader())
                .partialSchema(partialSchema)
                .conversionProvider(getConverter())
                .make();

        return StreamHows.fromReaderHow(dataInCsv);
    }

    public Function<String, DidoData> toMapFromString() {

        return DataInCsv.with()
                .csvFormat(getCsvFormat())
                .schema(schema)
                .header(isWithHeader())
                .partialSchema(partialSchema)
                .conversionProvider(getConverter())
                .mapFromString();
    }


    public void setSchema(DataSchema schema) {
        this.schema = schema;
    }

    public DataSchema getSchema() {
        return schema;
    }

    public boolean isPartialSchema() {
        return partialSchema;
    }

    public void setPartialSchema(boolean partialSchema) {
        this.partialSchema = partialSchema;
    }

    @Override
    public String toString() {
        return "CsvDataInBean{csvFormat=" + getCsvFormat()  +
                ", withHeader=" + isWithHeader() + '}';
    }
}
