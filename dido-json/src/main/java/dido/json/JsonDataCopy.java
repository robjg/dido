package dido.json;

import com.google.gson.*;
import dido.data.*;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Adds Deserialisers to a GsonBuilder for create DidoData.
 * Done in this way to support nested JSON. This is no longer used by
 * {@link DataInJson} but is here in case useful one day.
 *
 */
public class JsonDataCopy {

    private final LinkedList<DataFactory> stack = new LinkedList<>();

    private final DataFactoryProvider dataFactoryProvider;

    private JsonDataCopy(DataSchema schema,
                         DataFactoryProvider dataFactoryProvider) {
        this.stack.addFirst(dataFactoryProvider.factoryFor(schema));
        this.dataFactoryProvider = dataFactoryProvider;
    }

    public static GsonBuilder registerSchema(GsonBuilder gsonBuilder,
                                                                  DataSchema schema,
                                                                  DataFactoryProvider dataFactoryProvider) {
        return new JsonDataCopy(schema, dataFactoryProvider)
                .init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        return gsonBuilder
                .registerTypeAdapter(DidoData.class, new DataDeserializer())
                .registerTypeAdapter(SchemaField.NESTED_REPEATING_TYPE, new RepeatingDeserializer());
    }

    class DataDeserializer implements JsonDeserializer<DidoData> {

        @Override
        public DidoData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject()) {
                throw new JsonParseException("JsonObject expected, received: " + json +
                        " (" + json.getClass().getSimpleName() + ")");
            }

            DataFactory dataFactory = stack.getFirst();
            WritableData setter = dataFactory.getWritableData();
            DataSchema schema = dataFactory.getSchema();

            JsonObject jsonObject = (JsonObject) json;

            for (SchemaField schemaField : schema.getSchemaFields()) {

                String field = schemaField.getName();

                JsonElement element = jsonObject.get(field);

                if (element == null) {
                    continue;
                }

                Type fieldType = schemaField.getType();
                if (schemaField.isNested()) {

                    DataSchema nestedSchema = schemaField.getNestedSchema();
                    stack.addFirst(dataFactoryProvider.factoryFor(nestedSchema));
                    if (schemaField.isRepeating()) {
                        fieldType = SchemaField.NESTED_REPEATING_TYPE;
                    }
                    else {
                        fieldType = SchemaField.NESTED_TYPE;
                    }
                }

                Object nested = context.deserialize(element, fieldType);
                setter.setNamed(field, nested);

                if (schemaField.isNested()) {

                    stack.removeFirst();
                }
            }

            return dataFactory.toData();
        }
    }

}
