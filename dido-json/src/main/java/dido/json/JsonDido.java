package dido.json;

import dido.data.ArrayDataDataFactoryProvider;
import dido.data.DataFactoryProvider;
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
     * @oddjob.description When reading data the JSON is copied or wrapped. The idea is that wrapping
     * data will be more performant for limited amounts of Data but tests really need to be done.
     * If no dataFactoryProvider is specified a default is used.
     * @oddjob.required No, defaults to false.
     */
    private boolean copy;

    /**
     * @oddjob.description Provide a data factory to use for the copy. If this is provided a copy rather
     * than wrapping is assumed.
     * @oddjob.required No, defaults to ArrayData, if copy is true.
     */
    private DataFactoryProvider dataFactoryProvider;

    public DataOutHow<OutputStream> toStreamOut() {

        return StreamHows.fromWriterHow(toWriterOut());
    }

    public DataOutHow<Writer> toWriterOut() {

        JsonDidoFormat format = Objects.requireNonNullElse(this.format, JsonDidoFormat.LINES);
        return DataOutJson.with()
                .schema(schema)
                .outFormat(format)
                .make();
    }

    public DataInHow<InputStream> toStreamIn() {
        return StreamHows.fromReaderHow(toReaderIn());
    }

    public DataInHow<Reader> toReaderIn() {

        JsonDidoFormat format = Objects.requireNonNullElse(this.format, JsonDidoFormat.LINES);

        DataFactoryProvider dataFactoryProvider = this.dataFactoryProvider == null && copy ?
                DataFactoryProvider.newInstance() : this.dataFactoryProvider;

        return DataInJson.with()
                .inFormat(format)
                .factoryProvider(dataFactoryProvider)
                .schema(schema)
                .partialSchema(partialSchema)
                .make();
    }

    private DataFactoryProvider dataFactoryProvider() {
        return this.dataFactoryProvider == null ? new ArrayDataDataFactoryProvider() : this.dataFactoryProvider;
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
