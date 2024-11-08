package dido.how.lines;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.ReadStrategy;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Streams  out from Dido data of a single field defaulting to the name of 'Line'.
 */
public class DataOutLines implements DataOutHow<Appendable> {

    private static final String LINE = "Line";

    private final String fieldName;

    private final FieldGetter fieldGetter;

    private DataOutLines(Settings settings) {
        this.fieldName = Objects.requireNonNullElse(settings.fieldName, LINE);
        this.fieldGetter = settings.schema == null ? null :
                fieldGetterFrom(settings.schema, this.fieldName);
    }

    public static class Settings {

        private String fieldName;

        private DataSchema schema;

        public Settings fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Settings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public DataOut toAppendable(Appendable appendable) {
            return make().outTo(appendable);
        }

        public DataOut toWriter(Writer writer) {
            return make().outTo((writer));
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

        public DataOutLines make() {
            return new DataOutLines(this);
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

    public static DataOutLines withDefaults() {
        return with().make();
    }

    @Override
    public Class<Appendable> getOutType() {
        return Appendable.class;
    }

    @Override
    public DataOut outTo(Appendable outTo) {

        return fieldGetter == null ?
                new UnKnownOut(fieldName, outTo) :
                new KnownOut(fieldGetter, outTo);
    }

    static FieldGetter fieldGetterFrom(DataSchema schema,
                                       String fieldName) {
        ReadStrategy readStrategy = ReadStrategy.fromSchema(schema);
        return readStrategy.getFieldGetterNamed(fieldName);
    }

    static class KnownOut implements DataOut {

        private final FieldGetter fieldGetter;

        private final Appendable out;

        KnownOut(FieldGetter fieldGetter,
                 Appendable outputStream) {

            this.fieldGetter = fieldGetter;
            this.out = outputStream;
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
        public void accept(DidoData data) {

            try {
                if (fieldGetter.has(data)) {
                    out.append(fieldGetter.get(data).toString());
                }
                out.append(System.lineSeparator());
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }
    }

    static class UnKnownOut implements DataOut {

        private final String fieldName;

        private final Appendable out;

        private DataSchema lastSchema;

        private DataOut known;

        UnKnownOut(String fieldName,
                   Appendable out) {
            this.fieldName = fieldName;
            this.out = out;
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
        public void accept(DidoData data) {
            DataSchema schema = data.getSchema();
            if (!schema.equals(lastSchema)) {
                known = new KnownOut(
                        fieldGetterFrom(schema, fieldName),
                        out);
                lastSchema = schema;
            }
            known.accept(data);
        }
    }
}
