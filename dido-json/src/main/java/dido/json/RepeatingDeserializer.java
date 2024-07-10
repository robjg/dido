package dido.json;

import com.google.gson.*;
import dido.data.DidoData;
import dido.data.RepeatingData;

import java.lang.reflect.Type;

class RepeatingDeserializer implements JsonDeserializer<RepeatingData> {

    private final Class<?> dataType;

    RepeatingDeserializer(Class<?> dataType) {
        this.dataType = dataType;
    }

    @Override
    public RepeatingData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (!json.isJsonArray()) {
            throw new JsonParseException("JsonArray expected, received: " + json +
                    " (" + json.getClass().getSimpleName() + ")");
        }

        JsonArray jsonArray = json.getAsJsonArray();

        DidoData[] result = new DidoData[jsonArray.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = context.deserialize(jsonArray.get(i), dataType);
        }

        return RepeatingData.of(result);
    }
}
