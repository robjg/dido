package dido.json;

import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.ToNumberPolicy;
import dido.data.DataSchema;
import dido.how.DataInHow;
import dido.how.DataOutHow;
import dido.how.StreamHows;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Provide a {@link DataInHow} and a {@link DataOutHow} for JSON.
 * Designed to be used as a bean in Oddjob.
 *
 * @oddjob.description Creates an In or an Out for JSON data. Data can either be in the format
 * of a single JSON Object per line. An array of JSON Objects, or A single JSON Object.
 *
 * @oddjob.example From JSON Lines and back again.
 * {@oddjob.xml.resource dido/json/FromToJsonExample.xml}
 *
 * @oddjob.example From JSON Array and back again.
 * {@oddjob.xml.resource dido/json/FromToJsonArrayExample.xml}
 * The output in results is:
 * {@oddjob.text.resource expected/FromToJsonArrayExample.json}
 *
 * @oddjob.example Json with Nulls and Special Floating Point Numbers. Without setting the properties
 * the jobs would fail.
 * {@oddjob.xml.resource dido/json/FromToJsonNullsAndNans.xml}
 * The captured data is:
 * {@oddjob.text.resource expected/FromToJsonNullsAndNansData.txt}
 * The output in results is:
 * {@oddjob.text.resource expected/FromToJsonNullsAndNans.json}
 *
 * @oddjob.example Configuring the Gson Builder directly using JavaScript.
 * {@oddjob.xml.resource dido/json/FromToWithGsonBuilder.xml}
 * The captured data is:
 * {@oddjob.text.resource expected/FromToWithGsonBuilderData.txt}
 * The output Json is:
 * {@oddjob.text.resource expected/FromToWithGsonBuilder.json}
 *
 */
public class JsonDido {

    /**
     * @oddjob.description The schema to use. When reading in, if one is not provided a simple schema will be
     * created based on the JSON primitive type. When writing out the schema will be used to limit the number
     * of fields written.
     * @oddjob.required No.
     */
    private DataSchema schema;

    /**
     * @oddjob.description When reading data in, indicates that the provided Schema is partial. The
     * rest of the schema will be taken from the data.
     * @oddjob.required No, defaults to false.
     */
    private boolean partialSchema;

    /**
     * @oddjob.description The format of the data. LINES, ARRAY, SINGLE.
     * @oddjob.required No, defaults to LINES.
     */
    private JsonDidoFormat format;

    /**
     * @oddjob.description Serialize null values. True to serialize null to the JSON,
     * false and they will be ignored and no field will be written.
     * @oddjob.required No, defaults to false.
     */
    private boolean serializeNulls;

    /**
     * @oddjob.description Serialize NaN and Infinity values. True to serialize, false
     * and these values in data will result in an Exception. Note that because of an
     * oversight in the underlying Gson implementation, this has the same effect as
     * setting Strictness to LENIENT.
     *
     * @oddjob.required No, defaults to false.
     */
    private boolean serializeSpecialFloatingPointValues;

    /**
     * @oddjob.description Configures Gson to apply a specific number strategy during deserialization of
     * number type primitives. This is what will be used for a partial or no schema when converting numbers.
     *
     * @oddjob.required No, defaults numbers as doubles.
     */
    private ToNumberPolicy objectToNumberPolicy;

    /**
     * @oddjob.description Gson Strictness passed through to underlying Gson builder.
     * @oddjob.required No, defaults to LEGACY_STRICT.
     */
    private Strictness strictness;

    /**
     * @oddjob.description Configure the Gson Builder directly. This property specifies any number of Consumers of
     * the Gson Builder. See the examples for using this with JavaScript.
     *
     * @oddjob.required No.
     */
    private final List<Consumer<? super GsonBuilder>> gsonBuilder = new ArrayList<>();

    public DataOutHow<OutputStream> toStreamOut() {

        return StreamHows.fromWriterHow(toWriterOut());
    }

    public DataOutHow<Writer> toWriterOut() {

        JsonDidoFormat format = Objects.requireNonNullElse(this.format, JsonDidoFormat.LINES);

        DataOutJson.Settings settings = DataOutJson.with()
                .schema(schema)
                .strictness(strictness)
                .outFormat(format);

        if (serializeSpecialFloatingPointValues) {
            settings.serializeSpecialFloatingPointValues();
        }

        if (serializeNulls) {
            settings.serializeNulls();
        }

        for (Consumer<? super GsonBuilder> builder : gsonBuilder) {
            settings.gsonBuilder(builder);
        }

        return settings.make();
    }

    public DataInHow<InputStream> toStreamIn() {
        return StreamHows.fromReaderHow(toReaderIn());
    }

    public DataInHow<Reader> toReaderIn() {

        JsonDidoFormat format = Objects.requireNonNullElse(this.format, JsonDidoFormat.LINES);

        DataInJson.Settings settings = DataInJson.with()
                .inFormat(format)
                .strictness(strictness)
                .schema(schema)
                .partialSchema(partialSchema);

        Optional.ofNullable(objectToNumberPolicy).ifPresent(settings::objectToNumberStrategy);

        for (Consumer<? super GsonBuilder> builder : gsonBuilder) {
            settings.gsonBuilder(builder);
        }

        return settings.make();
    }

    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
        this.schema = schema;
    }

    public boolean isPartialSchema() {
        return partialSchema;
    }

    public void setPartialSchema(boolean partialSchema) {
        this.partialSchema = partialSchema;
    }

    public JsonDidoFormat getFormat() {
        return format;
    }

    public void setFormat(JsonDidoFormat format) {
        this.format = format;
    }

    public boolean isSerializeNulls() {
        return serializeNulls;
    }

    public void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    public boolean isSerializeSpecialFloatingPointValues() {
        return serializeSpecialFloatingPointValues;
    }

    public void setSerializeSpecialFloatingPointValues(boolean serializeSpecialFloatingPointValues) {
        this.serializeSpecialFloatingPointValues = serializeSpecialFloatingPointValues;
    }

    public ToNumberPolicy getObjectToNumberPolicy() {
        return objectToNumberPolicy;
    }

    public void setObjectToNumberPolicy(ToNumberPolicy objectToNumberPolicy) {
        this.objectToNumberPolicy = objectToNumberPolicy;
    }

    public Strictness getStrictness() {
        return strictness;
    }

    public void setStrictness(Strictness strictness) {
        this.strictness = strictness;
    }

    public void setGsonBuilder(int index, Consumer<? super GsonBuilder> withBuilder) {
        if (withBuilder == null) {
            gsonBuilder.remove(index);
        }
        else {
            gsonBuilder.add(index, withBuilder);
        }
    }

    @Override
    public String toString() {
        return "JsonHow{" +
                "format=" + format +
                '}';
    }
}
