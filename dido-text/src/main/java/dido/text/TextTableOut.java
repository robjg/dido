package dido.text;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.how.util.Primitives;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

public class TextTableOut implements DataOutHow<OutputStream> {

    private final DataSchema schema;

    private TextTableOut(Options options) {
        this.schema = options.schema;
    }

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    public static Options ofOptions() {
        return new Options();
    }

    public static class Options {

        private DataSchema schema;

        public Options schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public DataOutHow<OutputStream> create() {
            return new TextTableOut(this);
        }
    }

    @Override
    public DataOut outTo(OutputStream dataOut) {

        return Optional.ofNullable(this.schema)
                .<DataOut>map(s -> new WithSchema(s, dataOut))
                .orElseGet(() -> new UnknownSchema(dataOut));
    }

    static class WithSchema implements DataOut {

        private final DataSchema schema;

        private final Table table;

        private final OutputStream output;

        WithSchema(DataSchema schema, OutputStream output) {
            this.schema = schema;
            this.output = output;

            this.table = new Table(schema.lastIndex(),
                    BorderStyle.CLASSIC,
                    ShownBorders.HEADER_AND_COLUMNS);

            for (int i = 1; i > 0; i = schema.nextIndex(i)) {
                String value = schema.getFieldNameAt(i);

                if (value != null) {
                    table.addCell(value,
                            styleFor(schema.getTypeAt(i)));
                }
                else {
                    table.addCell("[" + i + "]",
                            styleFor(schema.getTypeAt(i)));
                }
            }
        }

        @Override
        public void close() {
            String[] rows = table.renderAsStringArray();
            PrintWriter writer = new PrintWriter(output);
            Arrays.stream(rows).forEach(writer::println);
            writer.close();
        }

        @Override
        public void accept(DidoData data) {
            for (int i = 1; i > 0; i = schema.nextIndex(i)) {
                Object value = data.getAt(i);
                if (value != null) {
                    table.addCell(value.toString(), styleFor(schema.getTypeAt(i)));
                } else {
                    table.addCell("");
                }
            }
        }
    }

    static class UnknownSchema implements DataOut {

        private final OutputStream outputStream;

        private DataOut delegate;

        UnknownSchema(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void close() throws Exception {
            if (delegate != null) {
                this.delegate.close();
            }
        }

        @Override
        public void accept(DidoData data) {
            if (delegate == null) {
                delegate = new WithSchema(data.getSchema(), outputStream);
            }
            delegate.accept(data);
        }
    }

    static protected CellStyle styleFor(Class<?> type) {

        if (Primitives.unwrap(type).isPrimitive()) {
            return new CellStyle(CellStyle.HorizontalAlign.RIGHT,
                    CellStyle.AbbreviationStyle.DOTS, CellStyle.NullStyle.EMPTY_STRING, false);
        }
        else {
            return new CellStyle(CellStyle.HorizontalAlign.LEFT,
                    CellStyle.AbbreviationStyle.DOTS, CellStyle.NullStyle.EMPTY_STRING, false);
        }
    }

}
