package dido.json;

import com.google.gson.Strictness;
import dido.data.DataSchema;
import dido.how.DataInHow;
import dido.how.DataOutHow;
import dido.how.StreamHows;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

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
     * @oddjob.description Gson Strictness passed through to underlying Gson builder.
     * @oddjob.required No, defaults to false.
     */
    private Strictness strictness;

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

        return settings.make();
    }

    public DataInHow<InputStream> toStreamIn() {
        return StreamHows.fromReaderHow(toReaderIn());
    }

    public DataInHow<Reader> toReaderIn() {

        JsonDidoFormat format = Objects.requireNonNullElse(this.format, JsonDidoFormat.LINES);

        return DataInJson.with()
                .inFormat(format)
                .strictness(strictness)
                .schema(schema)
                .partialSchema(partialSchema)
                .make();
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

    public Strictness getStrictness() {
        return strictness;
    }

    public void setStrictness(Strictness strictness) {
        this.strictness = strictness;
    }

    @Override
    public String toString() {
        return "JsonHow{" +
                "format=" + format +
                '}';
    }
}
