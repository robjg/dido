package dido.json;

import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.ToNumberPolicy;
import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @oddjob.description Provides a Mapping Function that will convert a GSON String
 * into Dido Data.
 * <p>
 * See also {@link ToJsonStringType}
 *
 * @oddjob.example From JSON Strings using a Mapping function and back again.
 * The {@code objectToNumberPolicy} is set so that the Qty is a long otherwise with no
 * schema it would be a double.
 * {@oddjob.xml.resource dido/json/FromJsonMapExample.xml}
 * The output Json is:
 * {@oddjob.text.resource dido/json/FromJsonMapExampleOut.json}
 *
 */
public class FromJsonStringType {

    /**
     * @oddjob.description The schema to use. If one is not provided a simple schema will be
     * created based on the JSON primitive type.
     * @oddjob.required No.
     */
    private DataSchema schema;

    /**
     * @oddjob.description Indicates that the provided Schema is partial. The
     * rest of the schema will be taken from the data.
     * @oddjob.required No, defaults to false.
     */
    private boolean partialSchema;

    /**
     * @oddjob.description Gson Strictness passed through to underlying Gson builder.
     * @oddjob.required No, defaults to Gson default, LEGACY_STRICT.
     */
    private Strictness strictness;

    /**
     * @oddjob.description Configures Gson to apply a specific number strategy during deserialization of
     * number type primitives. This is what will be used for a partial or no schema when converting numbers.
     *
     * @oddjob.required No, defaults numbers as doubles.
     */
    private ToNumberPolicy objectToNumberPolicy;

    /**
     * @oddjob.description Configure the Gson Builder directly. This property specifies any number of Consumers of
     * the Gson Builder. See the examples for using this with JavaScript.
     *
     * @oddjob.required No.
     */
    private final List<Consumer<? super GsonBuilder>> gsonBuilder = new ArrayList<>();


    public Function<String, DidoData> toFunction() {

        DataInJson.Settings settings =  DataInJson.with()
                .schema(schema)
                .partialSchema(partialSchema)
                .strictness(strictness);

        Optional.ofNullable(objectToNumberPolicy).ifPresent(settings::objectToNumberStrategy);

        for (Consumer<? super GsonBuilder> builder : gsonBuilder) {
            settings.gsonBuilder(builder);
        }

        return settings.mapFromString();
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

    public Strictness getStrictness() {
        return strictness;
    }

    public void setStrictness(Strictness strictness) {
        this.strictness = strictness;
    }

    public ToNumberPolicy getObjectToNumberPolicy() {
        return objectToNumberPolicy;
    }

    public void setObjectToNumberPolicy(ToNumberPolicy objectToNumberPolicy) {
        this.objectToNumberPolicy = objectToNumberPolicy;
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
        return "From JSON String";
    }
}
