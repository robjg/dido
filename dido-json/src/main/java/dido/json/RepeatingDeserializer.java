package dido.json;

import com.google.gson.*;
import dido.data.GenericData;
import dido.data.RepeatingData;

import java.lang.reflect.Type;

class RepeatingDeserializer implements JsonDeserializer<RepeatingData<String>> {

    @Override
    public RepeatingData<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (!json.isJsonArray()) {
            throw new JsonParseException("JsonArray expected, received: " + json +
                    " (" + json.getClass().getSimpleName() + ")");
        }

        JsonArray jsonArray = json.getAsJsonArray();

        GenericData<String>[] result = new GenericData[jsonArray.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = context.deserialize(jsonArray.get(i), GenericData.class);
        }

        return RepeatingData.of(result);
    }
}
