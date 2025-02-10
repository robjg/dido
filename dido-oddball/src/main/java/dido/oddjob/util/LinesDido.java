package dido.oddjob.util;

import dido.data.DidoData;
import dido.how.DataInHow;
import dido.how.DataOutHow;
import dido.how.StreamHows;
import dido.how.lines.DataInLines;
import dido.how.lines.DataOutLines;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @oddjob.description Creates an In or Out for Lines of Text. The {@link DidoData} is created or expected to have
 * a field with the give field name or the name 'Line'.
 */
public class LinesDido {

    /**
     * @oddjob.property
     * @oddjob.description The name of the field for the data.
     * @oddjob.required No. defaults to Line.
     */
    private String fieldName;

    public DataOutHow<OutputStream> toStreamOut() {

        DataOutLines dataOutLines = DataOutLines.with()
                .fieldName(fieldName)
                .make();

        return StreamHows.fromWriterHow(dataOutLines);
    }

    public DataInHow<InputStream> toStreamIn() {

        DataInLines dataInLines =  DataInLines.with()
                .fieldName(fieldName)
                .make();

        return StreamHows.fromReaderHow(dataInLines);
    }

    @Override
    public String toString() {
        return "LinesHowType{" +
                "fieldName='" + fieldName + '\'' +
                '}';
    }
}
