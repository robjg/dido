package dido.json;

import dido.data.DataSchema;
import dido.data.GenericData;

import java.util.function.Function;

public class FromJsonStringType {

    private DataSchema<String> schema;

    private boolean partialSchema;

    private boolean copy;

    public Function<String, GenericData<String>> toFunction() {

        if (copy) {
            if (schema == null || partialSchema) {
                return JsonStringToData.asCopyWithPartialSchema(schema);
            }
            else {
                return JsonStringToData.asCopyWithSchema(schema);
            }
        }
        else {
            if (schema == null || partialSchema) {
                return JsonStringToData.asWrapperWithPartialSchema(schema);
            }
            else {
                return JsonStringToData.asWrapperWithSchema(schema);
            }
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

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    @Override
    public String toString() {
        return "From JSON String";
    }
}
