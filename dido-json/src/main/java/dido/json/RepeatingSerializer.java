package dido.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dido.data.DidoData;
import dido.data.RepeatingData;

import java.lang.reflect.Type;

/**
 * Gson serializer for {@link RepeatingData}s.
 */
public class RepeatingSerializer implements JsonSerializer<RepeatingData> {

    @Override
    public JsonElement serialize(RepeatingData src, Type typeOfSrc, JsonSerializationContext context) {

        JsonArray jsonArray = new JsonArray();

        for (DidoData element : src) {

            jsonArray.add(context.serialize(element));
        }

        return jsonArray;
    }

}
