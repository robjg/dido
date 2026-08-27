package dido.how.lines;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.ReadStrategy;
import dido.data.schema.HasSchema;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.RefinableFunction;
import dido.how.RefinableOutHow;
import dido.how.useful.AppendableOutHow;
import dido.how.useful.UnknownDataOut;
import dido.how.useful.UnknownFunction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

/**
 * Streams  out from Dido data of a single field defaulting to the name of 'Line'.
 */
public class DataOutLines implements RefinableOutHow<Appendable> {

    private static final String LINE = "Line";

    private final String fieldName;

    private DataOutLines(Settings settings) {
        this.fieldName = Objects.requireNonNullElse(settings.fieldName, LINE);
    }

    public static class Settings {

        private String fieldName;

        public Settings fieldName(String fieldName) {
            this.fieldName = fieldName;
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

        public RefinableFunction<DidoData, String> mapToString() {

            return make().mapToStringHow();
        }

        public DataOutLines make() {
            return new DataOutLines(this);
        }

        public KnownOutHow forSchema(DataSchema schema) {
            return make().forSchema(schema);
        }
    }

    public static Function<DidoData, String> mapToString() {

        return with().mapToString();
    }

    public static Settings with() {
        return new Settings();
    }

    public static DataOutLines withDefaults() {
        return with().make();
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

    public class KnownOutHow extends AppendableOutHow {

        private final DataSchema schema;

        KnownOutHow(DataSchema schema) {
            this.schema = schema;
        }

        @Override
        public Class<Appendable> getOutType() {
            return DataOutLines.this.getOutType();
        }

        @Override
        public DataOut outTo(Appendable outTo) {
            return new KnownOut(schema, fieldName, outTo);
        }

        @Override
        public String toString() {
            return DataOutLines.this.toString();
        }

        public Function<DidoData, String> mapToStringHow() {
            return DataOutLines.this.mapToStringHow().forSchema(schema);
        }
    }

    @Override
    public KnownOutHow forSchema(DataSchema schema) {
        return new KnownOutHow(schema);
    }

    @Override
    public Class<Appendable> getOutType() {
        return Appendable.class;
    }

    @Override
    public DataOut outTo(Appendable outTo) {

        return UnknownDataOut.outToOf(outTo, this);
    }

    public RefinableFunction<DidoData, String> mapToStringHow() {

        return UnknownFunction.of(
                schema -> new KnownOut(schema, fieldName, null));
    }

    static class KnownOut implements DataOut,
            Function<DidoData, String>, HasSchema {

        private final DataSchema schema;

        private final FieldGetter fieldGetter;

        private final Appendable out;

        KnownOut(DataSchema schema,
                 String fieldName,
                 Appendable outputStream) {

            this.schema = schema;
            ReadStrategy readStrategy = ReadStrategy.fromSchema(schema);
            this.fieldGetter = readStrategy.getFieldGetterNamed(fieldName);
            this.out = outputStream;
        }

        @Override
        public DataSchema getSchema() {
            return schema;
        }

        @Override
        public void close() {
            if (out instanceof Closeable) {
                try {
                    ((Closeable) out).close();
                } catch (IOException e) {
                    throw DataException.of(e);
                }
            }
        }

        @Override
        public void accept(DidoData didoData) {

            try {
                String s = apply(didoData);
                if (s != null) {
                    out.append(s);
                }
                out.append(System.lineSeparator());
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        @Override
        public String apply(DidoData didoData) {
            if (fieldGetter.has(didoData)) {
                return fieldGetter.get(didoData).toString();
            }
            else {
                return null;
            }
        }
    }
}
