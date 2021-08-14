package dido.json;

import dido.data.DataSchema;
import org.oddjob.dido.StreamIn;
import org.oddjob.dido.StreamOut;

public class JsonDido {

    private DataSchema<String> schema;

    private boolean partialSchema;

    public StreamOut<String> toStreamOut() {

        return new StreamOutJson();
    }

    public StreamIn<String> toStreamIn() {

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
