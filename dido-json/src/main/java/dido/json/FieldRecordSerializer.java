package dido.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dido.data.DataSchema;
import dido.data.GenericData;

import java.lang.reflect.Type;

/**
 * Gson serializer for {@link GenericData}s.
 */
public class FieldRecordSerializer implements JsonSerializer<GenericData<String>> {

    @Override
    public JsonElement serialize(GenericData<String> src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        DataSchema<String> schema = src.getSchema();
        for (String field: schema.getFields()) {
            if (src.hasField(field)) {
                jsonObject.add(field, context.serialize(src.getObject(field)));
            }
        }

        return jsonObject;
    }

}
