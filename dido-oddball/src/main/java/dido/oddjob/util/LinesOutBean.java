package dido.oddjob.util;

import dido.data.DidoData;
import dido.how.DataOutHow;
import dido.how.StreamHows;
import dido.how.lines.DataOutLines;

import java.io.OutputStream;
import java.util.function.Function;

/**
 * @oddjob.description Creates an Out for Lines of Text. The line is created from
 * a field of the {@link DidoData} with the given field name or the name 'Line'.
 */
public class LinesOutBean extends LinesBeanBase {

    public DataOutHow<OutputStream> toStreamOut() {

        DataOutLines dataOutLines = DataOutLines.with()
                .fieldName(getFieldName())
                .make();

        return StreamHows.fromWriterHow(dataOutLines);
    }

    public Function<DidoData, String> toMapToString() {

        return DataOutLines.with()
                .fieldName(getFieldName())
                .mapToString();
    }

    @Override
    public String toString() {
        return "LinesOutBean{" +
                "fieldName='" + getFieldName() + '\'' +
                '}';
    }
}
