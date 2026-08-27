package dido.oddjob.util;

import dido.data.DidoData;
import dido.how.DataInHow;
import dido.how.StreamHows;
import dido.how.lines.DataInLines;

import java.io.InputStream;
import java.util.function.Function;

/**
 * @oddjob.description Creates an In for Lines of Text. The {@link DidoData} is created
 * with a single field with the given field name or the name 'Line'.
 */
public class LinesInBean extends LinesBeanBase {

    public DataInHow<InputStream> toStreamIn() {

        DataInLines dataInLines =  DataInLines.with()
                .fieldName(getFieldName())
                .make();

        return StreamHows.fromReaderHow(dataInLines);
    }

    public Function<String, DidoData> toMapFromString() {

        return DataInLines.with()
                .fieldName(getFieldName())
                .mapFromString();
    }

    @Override
    public String toString() {
        return "LinesInBean{" +
                "fieldName='" + getFieldName() + '\'' +
                '}';
    }
}
