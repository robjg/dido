package dido.json;

import dido.data.DataSchema;
import dido.how.DataInHow;
import dido.how.DataOutHow;

import java.io.InputStream;
import java.io.OutputStream;

public class JsonDido {

    private DataSchema<String> schema;

    private boolean partialSchema;

    private boolean arrayFormat;

    public DataOutHow<String, OutputStream> toStreamOut() {

        return arrayFormat ? new StreamOutJson() : new StreamOutJsonLines();
    }

    public DataInHow<String, InputStream> toStreamIn() {

        return arrayFormat ? new StreamInJson(schema, partialSchema)
                : new StreamInJsonLines(schema, partialSchema);
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

    public boolean isArrayFormat() {
        return arrayFormat;
    }

    public void setArrayFormat(boolean arrayFormat) {
        this.arrayFormat = arrayFormat;
    }

    @Override
    public String toString() {
        return "JsonHow{" +
                "arrayFormat=" + arrayFormat +
                '}';
    }
}
