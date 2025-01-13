package dido.text;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.how.util.IoUtil;
import dido.how.util.Primitives;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Data out to an Ascii Text Table.
 */
public class DataOutTextTable implements DataOutHow<Appendable> {

    private final DataSchema schema;

    private final String lineSeparator;

    private final ShownBorders shownBorders;

    private final BorderStyle borderStyle;

    private DataOutTextTable(Settings settings) {
        this.schema = settings.schema;
        this.lineSeparator = Objects.requireNonNullElse(settings.lineSeparator, System.lineSeparator());
        this.shownBorders = Objects.requireNonNullElse(settings.shownBorders, ShownBorders.HEADER_AND_COLUMNS);
        this.borderStyle = Objects.requireNonNullElse(settings.borderStyle, BorderStyle.CLASSIC);
    }

    public static class Settings {

        private DataSchema schema;

        private String lineSeparator;

        private ShownBorders shownBorders;

        private BorderStyle borderStyle;

        public Settings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public Settings lineSeparator(String lineSeparator) {
            this.lineSeparator = lineSeparator;
            return this;
        }

        public Settings shownBorders(ShownBorders shownBorders) {
            this.shownBorders = shownBorders;
            return this;
        }

        public Settings borderStyle(BorderStyle borderStyle) {
            this.borderStyle = borderStyle;
            return this;
        }

        public DataOut toAppendable(Appendable appendable) {
            return make().outTo(appendable);
        }

        public DataOut toWriter(Writer writer) {
            return make().outTo(writer);
        }

        public DataOut toPath(Path path) {
            try {
                return make().outTo(Files.newBufferedWriter(path));
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataOut toOutputStream(OutputStream outputStream) {

            return make().outTo(new OutputStreamWriter(outputStream));
        }

        public DataOutTextTable make() {
            return new DataOutTextTable(this);
        }
    }

    public static DataOut toAppendable(Appendable appendable) {
        return with().toAppendable(appendable);
    }

    public static DataOut toWriter(Writer writer) {
        return with().toWriter(writer);
    }

    public static DataOut toPath(Path path) {
        return with().toPath(path);
    }

    public static DataOut toOutputStream(OutputStream outputStream) {

        return with().toOutputStream(outputStream);
    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<Appendable> getOutType() {
        return Appendable.class;
    }

    @Override
    public DataOut outTo(Appendable outTo) {

        return Optional.ofNullable(this.schema)
                .<DataOut>map(s -> new WithSchema(s, outTo))
                .orElseGet(() -> new UnknownSchema(outTo));
    }

    class WithSchema implements DataOut {

        private final DataSchema schema;

        private final Table table;

        private final Appendable output;

        WithSchema(DataSchema schema, Appendable output) {
            this.schema = schema;
            this.output = output;

            this.table = new Table(schema.lastIndex(),
                    borderStyle,
                    shownBorders);

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
            try (AutoCloseable ignore = IoUtil.closeableOf(output)) {
                for (String row : rows) {
                    output.append(row)
                            .append(lineSeparator);
                }
            }
            catch (Exception e) {
                throw DataException.of(e);
            }
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

    class UnknownSchema implements DataOut {

        private final Appendable appendable;

        private DataOut delegate;

        UnknownSchema(Appendable appendable) {
            this.appendable = appendable;
        }

        @Override
        public void close() {
            if (delegate != null) {
                this.delegate.close();
            }
        }

        @Override
        public void accept(DidoData data) {
            if (delegate == null) {
                delegate = new WithSchema(data.getSchema(), appendable);
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
