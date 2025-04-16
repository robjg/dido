package dido.json;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonWriter;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.how.conversion.DidoConversionProvider;
import dido.how.util.IoUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;


public class DataOutJson implements DataOutHow<Writer> {

    private final DataOutHow<Writer> delegate;

    private DataOutJson(DataOutHow<Writer> delegate) {
        this.delegate = delegate;
    }

    public static class Settings {

        private JsonDidoFormat didoFormat;

        private FormattingStyle formattingStyle = FormattingStyle.COMPACT;

        private DataSchema schema;

        private final GsonBuilder gsonBuilder = new GsonBuilder();

        private final DidoConversionAdaptorFactory.Settings didoConversion =
                DidoConversionAdaptorFactory.with();

        public Settings gsonBuilder(Consumer<? super GsonBuilder> withBuilder) {
            withBuilder.accept(gsonBuilder);
            return this;
        }

        public Settings strictness(Strictness strictness) {
            gsonBuilder.setStrictness(strictness == null ? Strictness.LEGACY_STRICT : strictness);
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

        public Settings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public Settings conversionProvider(DidoConversionProvider conversionProvider) {
            didoConversion.conversionProvider(conversionProvider);
            return this;
        }

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

        private JsonWriterWrapperProvider writerProvider() {

            DidoConversionAdaptorFactory didoConversionAdaptorFactory = didoConversion.make();
            if (!didoConversionAdaptorFactory.isEmpty()) {
                gsonBuilder.registerTypeAdapterFactory(didoConversionAdaptorFactory);
            }

            Gson gson = gsonBuilder.create();

            DidoJsonWriter didoWriter = schema == null
                    ? DidoJsonWriters.forUnknownSchema(gson)
                    : DidoJsonWriters.forSchema(schema, gson);

            return writer -> {
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
                };
            };
        }

        public DataOutJson make() {

            if (didoFormat == JsonDidoFormat.LINES) {

                String lineSeparator = this.formattingStyle.getNewline();
                if (lineSeparator.isEmpty()) {
                    lineSeparator = "\n";
                }
                FormattingStyle formattingStyle = this.formattingStyle.withNewline("");
                gsonBuilder.setFormattingStyle(formattingStyle);

                return new DataOutJson(new DataOutJsonLines(writerProvider(), lineSeparator));
            }

            gsonBuilder.setFormattingStyle(formattingStyle);

            if (didoFormat == JsonDidoFormat.ARRAY) {
                return new DataOutJson(new DataOutJsonWriter(writerProvider(), true));
            } else {
                return new DataOutJson(new DataOutJsonWriter(writerProvider(), false));
            }
        }

        public Function<DidoData, String> mapToString() {

            FormattingStyle formattingStyle = this.formattingStyle.withNewline("");
            gsonBuilder.setFormattingStyle(formattingStyle);

            JsonWriterWrapperProvider wrapperProvider = writerProvider();

            return data -> {
                Writer writer = new StringWriter();
                try {
                    wrapperProvider.writerFor(writer).write(data);
                } catch (IOException e) {
                    throw new DataException(e);
                }
                return writer.toString();
            };
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

    public static Function<DidoData, String> mapToString() {
        return with().mapToString();
    }

    public static Settings with() {
        return new Settings();
    }


    @Override
    public Class<Writer> getOutType() {
        return Writer.class;
    }

    @Override
    public DataOut outTo(Writer outTo) {

        return delegate.outTo(outTo);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}

