package dido.json;

import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.function.Function;

/**
 *
 * @oddjob.description Provides a Mapping Function that will convert a GSON String
 * into Dido Data.
 *
 * @oddjob.example From JSON Strings using a Mapping function and back again.
 * {@oddjob.xml.resource dido/json/FromJsonMapExample.xml}
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


    public Function<String, DidoData> toFunction() {

        return DataInJson.with()
                .schema(schema)
                .partialSchema(partialSchema)
                .mapFromString();
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

    @Override
    public String toString() {
        return "From JSON String";
    }
}
