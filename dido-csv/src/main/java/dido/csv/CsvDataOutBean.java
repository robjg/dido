package dido.csv;

import dido.data.DidoData;
import dido.how.RefinableFunction;
import dido.how.RefinableOutHow;
import dido.how.StreamHows;

import java.io.OutputStream;

/**
 * @oddjob.description Writes CSV format from Dido Data.
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
public class CsvDataOutBean extends CsvBeanBase {

    public RefinableOutHow<OutputStream> toStreamOut() {

        DataOutCsv.UnknownHow dataOutCsv = DataOutCsv.with()
                .csvFormat(getCsvFormat())
                .header(isWithHeader())
                .make();

        return StreamHows.fromRefinableWriterHow(dataOutCsv);
    }

    public RefinableFunction<DidoData, String> toMapToString() {

        return DataOutCsv.with()
                .csvFormat(getCsvFormat())
                .header(isWithHeader())
                .mapToString();
    }

    @Override
    public String toString() {
        return "CsvDataOutBean{csvFormat=" + getCsvFormat() +
                ", withHeader=" + isWithHeader() + '}';
    }
}
