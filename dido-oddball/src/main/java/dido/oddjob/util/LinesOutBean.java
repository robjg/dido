package dido.oddjob.util;

import dido.data.DidoData;
import dido.how.RefinableOutHow;
import dido.how.StreamHows;
import dido.how.lines.DataOutLines;

import java.io.OutputStream;
import java.util.function.Function;

/**
 * @oddjob.description Creates an Out for Lines of Text. The line is created from
 * a field of the {@link DidoData} with the given field name or the name 'Line'.
 */
public class LinesOutBean extends LinesBeanBase {

    public RefinableOutHow<OutputStream> toStreamOut() {

        RefinableOutHow<Appendable> dataOutLines = DataOutLines.with()
                .fieldName(getFieldName())
                .make();

        return StreamHows.fromRefinableWriterHow(dataOutLines);
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
