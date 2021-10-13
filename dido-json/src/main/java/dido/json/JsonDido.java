package dido.json;

import dido.data.DataSchema;
import dido.how.DataInHow;
import dido.how.DataOutHow;

import java.io.InputStream;
import java.io.OutputStream;

public class JsonDido {

    private DataSchema<String> schema;

    private boolean partialSchema;

    public DataOutHow<String, OutputStream> toStreamOut() {

        return new StreamOutJson();
    }

    public DataInHow<String, InputStream> toStreamIn() {

        return new StreamInJson(schema, partialSchema);
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
}
