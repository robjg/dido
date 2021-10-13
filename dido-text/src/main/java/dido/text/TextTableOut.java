package dido.text;

import dido.data.DataSchema;
import dido.data.GenericData;
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

public class TextTableOut<F> implements DataOutHow<F, OutputStream> {

    private final DataSchema<F> schema;

    private TextTableOut(Options<F> options) {
        this.schema = options.schema;
    }

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    public static <F> Options<F> ofOptions() {
        return new Options<>();
    }

    public static class Options<F> {

        private DataSchema<F> schema;

        public Options<F> schema(DataSchema<F> schema) {
            this.schema = schema;
            return this;
        }

        public DataOutHow<F, OutputStream> create() {
            return new TextTableOut<>(this);
        }
    }

    @Override
    public DataOut<F> outTo(OutputStream dataOut) {

        return Optional.ofNullable(this.schema)
                .<DataOut<F>>map(s -> new WithSchema<>(s, dataOut))
                .orElseGet(() -> new UnknownSchema<>(dataOut));
    }

    static class WithSchema<F> implements DataOut<F> {

        private final DataSchema<F> schema;

        private final Table table;

        private final OutputStream output;

        WithSchema(DataSchema<F> schema, OutputStream output) {
            this.schema = schema;
            this.output = output;

            this.table = new Table(schema.lastIndex(),
                    BorderStyle.CLASSIC,
                    ShownBorders.HEADER_AND_COLUMNS);

            for (int i = 1; i > 0; i = schema.nextIndex(i)) {
                F value = schema.getFieldAt(i);

                if (value != null) {
                    table.addCell(value.toString(),
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
        public void accept(GenericData<F> data) {
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

    static class UnknownSchema<F> implements DataOut<F> {

        private final OutputStream outputStream;

        private DataOut<F> delegate;

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
        public void accept(GenericData<F> data) {
            if (delegate == null) {
                delegate = new WithSchema<>(data.getSchema(), outputStream);
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
