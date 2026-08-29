package dido.json;

import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataInHow;
import dido.how.StreamHows;

import java.io.InputStream;
import java.io.Reader;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provide a {@link DataInHow} for JSON.
 * Designed to be used as a bean in Oddjob.
 *
 * @oddjob.description Creates an In for JSON data. Data can either be in the format
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
public class JsonDataInBean extends JsonDidoBase {


    /**
     * @oddjob.description The schema to use. If one is not provided a simple schema will be
     * created based on the JSON primitive type.
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
     * @oddjob.description Configures Gson to apply a specific number strategy during deserialization of
     * number type primitives. This is what will be used for a partial or no schema when converting numbers.
     *
     * @oddjob.required No, defaults numbers as doubles.
     */
    private ToNumberPolicy objectToNumberPolicy;

    public DataInHow<InputStream> toStreamIn() {
        return StreamHows.fromReaderHow(toReaderIn());
    }

    public DataInHow<Reader> toReaderIn() {

        JsonDidoFormat format = Objects.requireNonNullElse(getFormat(), JsonDidoFormat.LINES);

        return settingsIn().inFormat(format)
                .make();
    }

    public Function<String, DidoData> toMapFromString() {

        return settingsIn().mapFromString();
    }

    private DataInJson.Settings settingsIn() {

        JsonDidoFormat format = Objects.requireNonNullElse(getFormat(), JsonDidoFormat.LINES);

        DataInJson.Settings settings = DataInJson.with()
                .inFormat(format)
                .strictness(getStrictness())
                .schema(schema)
                .partialSchema(partialSchema);

        Optional.ofNullable(objectToNumberPolicy).ifPresent(settings::objectToNumberStrategy);

        loadConversions(settings);

        for (Consumer<? super GsonBuilder> builder : gsonBuilder) {
            settings.gsonBuilder(builder);
        }

        return settings;
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


    public ToNumberPolicy getObjectToNumberPolicy() {
        return objectToNumberPolicy;
    }

    public void setObjectToNumberPolicy(ToNumberPolicy objectToNumberPolicy) {
        this.objectToNumberPolicy = objectToNumberPolicy;
    }

    @Override
    public String toString() {
        return "JsonHow{" +
                "format=" + getFormat() +
                ", partialSchema=" + partialSchema +
                ", schema=" + getSchema() +
                '}';
    }
}
