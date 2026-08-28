package dido.json;

import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import dido.data.DataSchema;
import dido.data.util.ClassUtils;
import dido.how.DataInHow;
import dido.how.DataOutHow;
import dido.how.conversion.DidoConversionProvider;

import javax.inject.Inject;
import java.util.*;
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

    // In and Out Properties

    /**
     * @oddjob.description The schema to use. When reading in, if one is not provided a simple schema will be
     * created based on the JSON primitive type. When writing out the schema will be used to limit the number
     * of fields written.
     * @oddjob.required No.
     */
    private DataSchema schema;

    /**
     * @oddjob.description The format of the data. LINES, ARRAY, SINGLE.
     * @oddjob.required No, defaults to LINES.
     */
    private JsonDidoFormat format;

    /**
     * @oddjob.description Gson Strictness passed through to underlying Gson builder.
     * @oddjob.required No, defaults to LEGACY_STRICT.
     */
    private Strictness strictness;

    /**
     * @oddjob.description A Conversion Provider used when Dido conversions are specified.
     * @oddjob.required No.
     */
    private DidoConversionProvider conversionProvider;

    /**
     * @oddjob.description Specify a Dido Conversion is to be used for the given
     * transformation pair of types. When reading in the key is given as the
     * type that Gson will provide from the JSON element, Typically, this will be
     * String, Double, Boolean, or Map. The value is the type Dido
     * will convert to, and will be the more complicated type. When writing
     * Data out, the key is the complex Dido type and the value is the simpler Gson
     * aware type.
     * @oddjob.required No.
     */
    private final Map<String, String> didoConversion = new HashMap<>();


    /**
     * @oddjob.description The class loader used to create the types for the specified
     * dido conversions.
     * @oddjob.required No. Set automatically by the framework.
     */
    private ClassLoader classLoader;

    /**
     * @oddjob.description Configure the Gson Builder directly. This property specifies any number of Consumers of
     * the Gson Builder. See the examples for using this with JavaScript.
     *
     * @oddjob.required No.
     */
    protected final List<Consumer<? super GsonBuilder>> gsonBuilder = new ArrayList<>();


    protected void loadConversions(InOutSettings<?> settings) {
        if (!didoConversion.isEmpty()) {
            ClassLoader classLoader = Objects.requireNonNullElseGet(this.classLoader,
                    () -> getClass().getClassLoader());
            settings.conversionProvider(Objects.requireNonNull(conversionProvider,
                    "Dido conversions need a Conversion Provider"));
            for (Map.Entry<String, String> didoConversion : didoConversion.entrySet()) {
                Class<?> fromClass;
                Class<?> toClass;
                try {
                    fromClass = ClassUtils.classFor(didoConversion.getKey(), classLoader);
                    toClass = ClassUtils.classFor(didoConversion.getValue(), classLoader);
                    settings.didoConversion(fromClass, toClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }


    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
        this.schema = schema;
    }

    public Strictness getStrictness() {
        return strictness;
    }

    public void setStrictness(Strictness strictness) {
        this.strictness = strictness;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Inject
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public DidoConversionProvider getConversionProvider() {
        return conversionProvider;
    }

    @Inject
    public void setConversionProvider(DidoConversionProvider conversionProvider) {
        this.conversionProvider = conversionProvider;
    }

    public void setDidoConversion(String fromClass, String toClass) {
        this.didoConversion.put(
                Objects.requireNonNull(fromClass), toClass);
    }

    public void setGsonBuilder(int index, Consumer<? super GsonBuilder> withBuilder) {
        if (withBuilder == null) {
            gsonBuilder.remove(index);
        }
        else {
            gsonBuilder.add(index, withBuilder);
        }
    }

    public JsonDidoFormat getFormat() {
        return format;
    }

    public void setFormat(JsonDidoFormat format) {
        this.format = format;
    }

}
