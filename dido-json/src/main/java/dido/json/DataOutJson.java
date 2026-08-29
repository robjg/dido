package dido.json;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonWriter;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.RefinableFunction;
import dido.how.RefinableOutHow;
import dido.how.useful.WriterOutHow;
import dido.how.util.IoUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;


public class DataOutJson {

    private final Gson gson;

    private final JsonDidoFormat didoFormat;

    private final FormattingStyle formattingStyle;

    private DataOutJson(Gson gson,
                        JsonDidoFormat didoFormat,
                        FormattingStyle formattingStyle) {
        this.gson = gson;
        this.didoFormat = didoFormat;
        this.formattingStyle = formattingStyle;
    }

    public static class Settings extends InOutSettings<Settings> {

        private FormattingStyle formattingStyle = FormattingStyle.COMPACT;

        @Override
        Settings self() {
            return this;
        }

        public Settings serializeSpecialFloatingPointValues() {
            // SerializeSpecialFloatingPointValues is not passed through to the writer
            // but Strictness is.
            gsonBuilder.serializeSpecialFloatingPointValues()
                    .setStrictness(Strictness.LENIENT);
            return this;
        }

        public Settings serializeNulls() {
            gsonBuilder.serializeNulls();
            return this;
        }

        public Settings pretty() {
            formattingStyle = FormattingStyle.PRETTY;
            return this;
        }

        public Settings indent(String indent) {
            this.formattingStyle = this.formattingStyle.withIndent(indent);
            return this;
        }

        public Settings lineSeparator(String lineSeparator) {
            this.formattingStyle = this.formattingStyle.withNewline(lineSeparator);
            return this;
        }

        public Settings withSpaceAfterSeparators(boolean spaceAfterSeparators) {
            this.formattingStyle = this.formattingStyle.withSpaceAfterSeparators(spaceAfterSeparators);
            return this;
        }

        public Settings outFormat(JsonDidoFormat didoFormat) {
            this.didoFormat = didoFormat;
            return this;
        }

        @Override
        public Settings didoConversion(Type from, Type to) {
            didoConversion.register(to, from);
            return this;
        }

        public DataOut toAppendable(Appendable appendable) {
            return toWriter(IoUtil.writerFromAppendable(appendable));
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
            return make().outTo(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        }

        public UnknownHow make() {

            registerGsonBuilderDefaults();

            gsonBuilder.setFormattingStyle(formattingStyle);

            return new DataOutJson(gsonBuilder.create(), didoFormat, formattingStyle)
                    .new UnknownHow();
        }

        public KnownOutHow forSchema(DataSchema schema) {
            return make().forSchema(schema);
        }

        public Function<DidoData, String> mapToString() {
            return make().mapToString();
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

    public static KnownOutHow forSchema(DataSchema schema) {
        return with().forSchema(schema);
    }


    abstract public class OutHow extends WriterOutHow {

        abstract protected DidoJsonWriter didoWriter(Gson gson);

        @Override
        public Class<Writer> getOutType() {
            return Writer.class;
        }

        @Override
        public DataOut outTo(Writer outTo) {
            try {

                if (didoFormat == JsonDidoFormat.LINES) {
                    Gson gson = DataOutJson.this.gson;
                    String lineSeparator = formattingStyle.getNewline();
                        if (lineSeparator.isEmpty()) {
                            lineSeparator = "\n";
                        }
                        else {
                            FormattingStyle replacementStyle = formattingStyle.withNewline("");
                            gson = gson.newBuilder().setFormattingStyle(replacementStyle).create();
                    }

                    return new DataOutJsonLines(outTo, didoWriter(gson), gson, lineSeparator);
                } else {

                    JsonWriterWrapper writerWrapper = jsonWriter(didoWriter(gson), outTo);

                    if (didoFormat == JsonDidoFormat.ARRAY) {
                        return new DataOutJsonWriter(writerWrapper, true);
                    } else {
                        return new DataOutJsonWriter(writerWrapper, false);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return DataOutJson.this.toString();
        }

        public Function<DidoData, String> mapToString() {

            final Gson gson;

            String lineSeparator = formattingStyle.getNewline();
            if (lineSeparator.isEmpty()) {
                gson = DataOutJson.this.gson;
            }
            else {
                FormattingStyle replacementStyle = formattingStyle.withNewline("");
                gson = DataOutJson.this.gson.newBuilder()
                        .setFormattingStyle(replacementStyle).create();
            }

            DidoJsonWriter didoJsonWriter = didoWriter(gson);

            return data -> {
                Writer writer = new StringWriter();
                try {
                    JsonWriter jsonWriter = gson.newJsonWriter(writer);
                    didoJsonWriter.write(data, jsonWriter);
                } catch (IOException e) {
                    throw new DataException(e);
                }
                return writer.toString();
            };
        }
    }

    public class UnknownHow extends OutHow implements RefinableOutHow<Writer> {

        @Override
        protected DidoJsonWriter didoWriter(Gson gson) {
            return DidoJsonWriters.forUnknownSchema(gson);
        }

        @Override
        public KnownOutHow forSchema(DataSchema schema) {
            return new KnownOutHow(schema);
        }

        public RefinableFunction<DidoData, String> mapToString() {

            Function<DidoData, String> mapToString = super.mapToString();

            return new RefinableFunction<>() {

                @Override
                public Function<DidoData, String> forSchema(DataSchema schema) {
                    return new KnownOutHow(schema).mapToString();
                }

                @Override
                public String apply(DidoData didoData) {
                    return mapToString.apply(didoData);
                }
            };
        }
    }

    public class KnownOutHow extends OutHow {

        final DataSchema schema;

        KnownOutHow(DataSchema schema) {
            this.schema = schema ;
        }

        @Override
        protected DidoJsonWriter didoWriter(Gson gson) {
            return DidoJsonWriters.forSchema(schema, gson);
        }
    }

    public static Function<DidoData, String> mapToString() {
        return with().mapToString();
    }

    private JsonWriterWrapper jsonWriter(DidoJsonWriter didoWriter,
                                         Writer writer) throws IOException {

        JsonWriter jsonWriter = gson.newJsonWriter(writer);

        return new JsonWriterWrapper() {
            @Override
            public void write(DidoData data) throws IOException {
                didoWriter.write(data, jsonWriter);
            }

            @Override
            public void close() throws IOException {
                jsonWriter.close();
            }

            @Override
            public JsonWriter getWrappedWriter() {
                return jsonWriter;
            }

            @Override
            public Writer getWriter() {
                return writer;
            }
        };
    }

    @Override
    public String toString() {
        return "JsonOut " + didoFormat;
    }
}

