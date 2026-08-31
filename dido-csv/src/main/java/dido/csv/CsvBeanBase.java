package dido.csv;

import dido.how.conversion.DidoConversionProvider;
import org.apache.commons.csv.CSVFormat;

/**
 * Common Base Class for CSV beans
 */
public class CsvBeanBase {

    /**
     * @oddjob.description The CSV Format to use. See the <a href="https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html">CSVFormat JavaDoc</a>
     * for more information.
     * @oddjob.required No.
     */
    private CSVFormat csvFormat;

    /**
     * @oddjob.description Does the data contain a header or is a header to be written.
     * @oddjob.required No, defaults to false.
     */
    private boolean withHeader;

    /** @oddjob.description A converter used to convert Strings to the required schema type.
     * Note Converter is only used for Input. Output is more complicated as the printer needs to know
     * if values should be quoted, so we can't pre convert to a String.
     * @oddjob.required No
     */
    private DidoConversionProvider converter;


    public CSVFormat getCsvFormat() {
        return csvFormat;
    }

    public void setCsvFormat(CSVFormat csvFormat) {
        this.csvFormat = csvFormat;
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
