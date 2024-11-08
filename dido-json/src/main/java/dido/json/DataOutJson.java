package dido.json;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.how.util.IoUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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


        public Settings gsonBuilder(Consumer<? super GsonBuilder> withBuilder) {
            withBuilder.accept(gsonBuilder);
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

        private void registerGsonBuilderDefaults() {

            gsonBuilder.registerTypeHierarchyAdapter(
                            DidoData.class,
                            schema == null ? DataSerializer.forUnknownSchema() :
                                    DataSerializer.forSchema(schema))
                    .serializeSpecialFloatingPointValues();
        }

        public DataOutJson make() {

            registerGsonBuilderDefaults();

            if (didoFormat == JsonDidoFormat.LINES) {

                String lineSeparator = this.formattingStyle.getNewline();
                if (lineSeparator.isEmpty()) {
                    lineSeparator = "\n";
                }
                FormattingStyle formattingStyle = this.formattingStyle.withNewline("");
                gsonBuilder.setFormattingStyle(formattingStyle);

                return new DataOutJson(new DataOutJsonLines(gsonBuilder.create(), lineSeparator));
            }

            gsonBuilder.setFormattingStyle(formattingStyle);

            if (didoFormat == JsonDidoFormat.ARRAY) {
                return new DataOutJson(new DataOutJsonWriter(gsonBuilder.create(), true));
            } else {
                return new DataOutJson(new DataOutJsonWriter(gsonBuilder.create(), false));
            }
        }

        public Function<DidoData, String> asMapperToString() {

            registerGsonBuilderDefaults();

            FormattingStyle formattingStyle = this.formattingStyle.withNewline("");
            gsonBuilder.setFormattingStyle(formattingStyle);
            Gson gson = gsonBuilder.create();

            return gson::toJson;
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

    public static Function<DidoData, String> asMapperToString() {
        return with().asMapperToString();
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

