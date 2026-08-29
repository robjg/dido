package dido.json;

import com.google.gson.GsonBuilder;
import dido.data.DidoData;
import dido.how.DataOutHow;
import dido.how.RefinableOutHow;
import dido.how.StreamHows;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provide a {@link DataOutHow} for JSON.
 * Designed to be used as a bean in Oddjob.
 *
 * @oddjob.description Creates an Out for JSON data. Data can either be in the format
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
public class JsonDataOutBean extends JsonDidoBase {


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

    // // // //

    public RefinableOutHow<OutputStream> toStreamOut() {

        return StreamHows.fromRefinableWriterHow(toWriterOut());
    }

    public RefinableOutHow<Writer> toWriterOut() {

        JsonDidoFormat format = Objects.requireNonNullElse(getFormat(), JsonDidoFormat.LINES);

        return settingsOut()
                .outFormat(format)
                .make();
    }

    public Function<DidoData, String> toMapToString() {

        return settingsOut()
                .mapToString();
    }

    private DataOutJson.Settings settingsOut() {

        DataOutJson.Settings settings = DataOutJson.with()
                .strictness(getStrictness());

        loadConversions(settings);

        if (serializeSpecialFloatingPointValues) {
            settings.serializeSpecialFloatingPointValues();
        }

        if (serializeNulls) {
            settings.serializeNulls();
        }

        for (Consumer<? super GsonBuilder> builder : gsonBuilder) {
            settings.gsonBuilder(builder);
        }

        return settings;
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

    @Override
    public String toString() {
        return "JsonHow{" +
                "format=" + getFormat() +
                '}';
    }
}
