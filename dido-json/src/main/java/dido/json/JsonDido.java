package dido.json;

import dido.data.DataSchema;
import dido.how.DataInHow;
import dido.how.DataOutHow;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Provide a {@link DataInHow} and a {@link DataOutHow} fro JSON. Designed to be used as a bean in Oddjob.
 */
public class JsonDido {

    enum Format {
        SINGLE,
        ARRAY,
        LINES,
    }

    private DataSchema<String> schema;

    private boolean partialSchema;

    private Format format;

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
                "arrayFormat=" + format +
                '}';
    }
}
