package dido.json;

import com.google.gson.*;
import dido.data.DataSchema;
import dido.data.GenericData;

import java.lang.reflect.Type;
import java.util.function.BiFunction;

/**
 * Gson deserializer for {@link GenericData}s.
 */
public class FieldRecordDeserializer implements JsonDeserializer<GenericData<String>> {

    private final BiFunction<JsonObject, JsonDeserializationContext, GenericData<String>> dataExtractor;

    public FieldRecordDeserializer(DataSchema<String> schema, boolean partialSchema) {
        if (partialSchema) {
            dataExtractor = (jsonObject, serializationContext) ->
                    JsonDataWrapper.from(
                                    JsonSchemaExtractor.from(schema)
                                            .fromElement(jsonObject))
                            .wrap(jsonObject, serializationContext);
        } else {
            dataExtractor = JsonDataWrapper.from(schema)::wrap;
        }
    }

    @Override
    public GenericData<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        return dataExtractor.apply(jsonObject, context);
    }
}
