package dido.json;

import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @oddjob.description Provides a Mapping Function that will convert a Dido Data
 * into a JSON String. See also {@link FromJsonStringType}
 *
 * @oddjob.example From JSON Strings using a Mapping function and back again.
 * {@oddjob.xml.resource dido/json/FromJsonMapExample.xml}
 * The output Json is:
 * {@oddjob.text.resource dido/json/FromJsonMapExampleOut.json}
 *
 */
public class ToJsonStringType {

    /**
     * @oddjob.description The schema to use. This schema will be used to limit the number
     * of fields written.
     * @oddjob.required No.
     */
    private DataSchema schema;

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
     * @oddjob.description Gson Strictness passed through to underlying Gson builder.
     * @oddjob.required No, defaults to Gson default, LEGACY_STRICT.
     */
    private Strictness strictness;

    /**
     * @oddjob.description Configure the Gson Builder directly. This property specifies any number of Consumers of
     * the Gson Builder. See the examples for using this with JavaScript.
     *
     * @oddjob.required No.
     */
    private final List<Consumer<? super GsonBuilder>> gsonBuilder = new ArrayList<>();


    public Function<DidoData, String> toFunction() {

        DataOutJson.Settings settings = DataOutJson.with()
                .strictness(strictness)
                .schema(schema);

        if (serializeSpecialFloatingPointValues) {
            settings.serializeSpecialFloatingPointValues();
        }

        if (serializeNulls) {
            settings.serializeNulls();
        }

        for (Consumer<? super GsonBuilder> builder : gsonBuilder) {
            settings.gsonBuilder(builder);
        }

        return settings.mapToString();
    }

    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
        this.schema = schema;
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
        return "To Json String";
    }
}
