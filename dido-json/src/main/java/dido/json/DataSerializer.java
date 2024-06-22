package dido.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dido.data.DataSchema;
import dido.data.DidoData;

import java.lang.reflect.Type;

/**
 * Gson serializer for {@link DidoData}s.
 */
public class DataSerializer implements JsonSerializer<DidoData> {

    @Override
    public JsonElement serialize(DidoData src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        DataSchema schema = src.getSchema();
        for (String field: schema.getFieldNames()) {
            if (src.hasNamed(field)) {
                jsonObject.add(field, context.serialize(src.getNamed(field)));
            }
        }

        return jsonObject;
    }

}
