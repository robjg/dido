package dido.json;

import com.google.gson.*;
import dido.data.*;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Adds Deserialisers to a GsonBuilder for create DidoData. Done in this way to support nested JSON.
 *
 * @param <D> The type of Data that will be produced.
 */
public class JsonDataCopy<D extends DidoData> {

    static class SchemaAndFactory<D extends DidoData> {

        final DataSchema schema;

        final DataFactory<D> dataFactory;

        SchemaAndFactory(DataSchema schema, DataFactory<D> dataFactory) {
            this.schema = schema;
            this.dataFactory = dataFactory;
        }
    }

    private final LinkedList<SchemaAndFactory<D>> stack = new LinkedList<>();

    private final DataFactoryProvider<D> dataFactoryProvider;

    private JsonDataCopy(DataSchema schema,
                         DataFactoryProvider<D> dataFactoryProvider) {
        this.stack.addFirst(new SchemaAndFactory<>(schema, dataFactoryProvider.provideFactory(schema)));
        this.dataFactoryProvider = dataFactoryProvider;
    }

    public static <D extends DidoData> GsonBuilder registerSchema(GsonBuilder gsonBuilder,
                                                                  DataSchema schema,
                                                                  DataFactoryProvider<D> dataFactoryProvider) {
        return new JsonDataCopy<>(schema, dataFactoryProvider)
                .init(gsonBuilder, dataFactoryProvider.getDataType());
    }

    private GsonBuilder init(GsonBuilder gsonBuilder, Type dataType) {
        return gsonBuilder
                .registerTypeAdapter(dataType, new DataDeserializer())
                .registerTypeAdapter(SchemaField.NESTED_REPEATING_TYPE, new RepeatingDeserializer(dataType));
    }

    class DataDeserializer implements JsonDeserializer<D> {

        @Override
        public D deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject()) {
                throw new JsonParseException("JsonObject expected, received: " + json +
                        " (" + json.getClass().getSimpleName() + ")");
            }

            SchemaAndFactory<D> schemaAndFactory = stack.getFirst();
            WritableData setter = schemaAndFactory.dataFactory.getWritableData();
            DataSchema schema = schemaAndFactory.schema;

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
                    stack.addFirst(new SchemaAndFactory<>(
                            nestedSchema, dataFactoryProvider.provideFactory(nestedSchema)));
                    if (schemaField.isRepeating()) {
                        fieldType = SchemaField.NESTED_REPEATING_TYPE;
                    }
                    else {
                        fieldType = dataFactoryProvider.getDataType();
                    }
                }

                Object nested = context.deserialize(element, fieldType);
                setter.setNamed(field, nested);

                if (schemaField.isNested()) {

                    stack.removeFirst();
                }
            }

            return schemaAndFactory.dataFactory.toData();
        }
    }

}
