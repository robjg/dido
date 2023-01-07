package dido.json;

import com.google.gson.*;
import dido.data.*;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Adds Deserialisers to a GsonBuilder for create GenericData. Done in this way to support nested JSON.
 */
public class JsonDataCopy {

    private final LinkedList<DataSchema<String>> stack = new LinkedList<>();

    private final GenericDataBuilder<String> builder;
    private JsonDataCopy(DataSchema<String> schema) {
        this.stack.addFirst(schema);
        this.builder = MapData.newBuilder(schema);
    }

    public static GsonBuilder registerSchema(GsonBuilder gsonBuilder,
                                             DataSchema<String> schema) {
        return new JsonDataCopy(schema).init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(GenericData.class,
                new DataDeserializer())
                .registerTypeAdapter(RepeatingData.class, new RepeatingDeserializer());
    }

    class DataDeserializer implements JsonDeserializer<GenericData<String>> {

        @Override
        public GenericData<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject()) {
                throw new JsonParseException("JsonObject expected, received: " + json +
                        " (" + json.getClass().getSimpleName() + ")");
            }

            DataSchema<String> schema = stack.getFirst();

            JsonObject jsonObject = (JsonObject) json;

            for (SchemaField<String> schemaField : schema.getSchemaFields()) {

                String field = schemaField.getField();

                JsonElement element = jsonObject.get(field);

                if (element == null) {
                    continue;
                }

                if (schemaField.isNested()) {

                    stack.addFirst(schemaField.getNestedSchema());
                }

                Object nested = context.deserialize(element, schemaField.getType());
                builder.set(field, nested);

                if (schemaField.isNested()) {

                    stack.removeFirst();
                }
            }

            return builder.build();
        }
    }

}
