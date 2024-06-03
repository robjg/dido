package dido.json;

import dido.data.DataSchema;
import dido.how.DataInHow;
import dido.how.DataOutHow;

import java.io.InputStream;
import java.io.OutputStream;
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

    public enum Format {
        SINGLE,
        ARRAY,
        LINES,
    }

    /**
     * @oddjob.description The schema to use. When reading in, if one is not provided a simple schema will be
     * created based on the JSON primitive type. When writing out the schema will be used to limit the number
     * of fields written.
     * @oddjob.required No.
     */
    private DataSchema<String> schema;

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
    private Format format;

    /**
     * @oddjob.description When reading data is the data copied or wrapped. The idea is that wrapping
     * data will be more performant for limited amounts of Data but tests really need to be done.
     * @oddjob.required No, defaults to false.
     */
    private boolean copy;

    public DataOutHow<String, OutputStream> toStreamOut() {

        Format format = Objects.requireNonNullElse(this.format, Format.LINES);
        switch (format) {
            case SINGLE:
                return StreamOutJson.streamOutSingle();
            case ARRAY:
                return StreamOutJson.streamOutArray();
            case LINES:
                return new StreamOutJsonLines();
            default:
                throw new IllegalArgumentException("Unknown " + format);
        }
    }

    public DataInHow<String, InputStream> toStreamIn() {

        Format format = Objects.requireNonNullElse(this.format, Format.LINES);
        switch (format) {
            case SINGLE:
            case ARRAY:
                return StreamInJson.settings()
                        .setSchema(schema)
                        .setPartial(partialSchema)
                        .setCopy(copy)
                        .setIsArray(format == Format.ARRAY)
                        .make();
            case LINES:
                return StreamInJsonLines.settings()
                        .setSchema(schema)
                        .setPartial(partialSchema)
                        .setCopy(copy)
                        .make();
            default:
                throw new IllegalArgumentException("Unknown " + format);
        }
    }

    public DataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(DataSchema<String> schema) {
        this.schema = schema;
    }

    public boolean isPartialSchema() {
        return partialSchema;
    }

    public void setPartialSchema(boolean partialSchema) {
        this.partialSchema = partialSchema;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    @Override
    public String toString() {
        return "JsonHow{" +
                "format=" + format +
                '}';
    }
}
