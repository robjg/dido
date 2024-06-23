package dido.json;

import com.google.gson.*;
import dido.data.*;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Adds Deserialisers to a GsonBuilder for create DidoData. Done in this way to support nested JSON.
 */
public class JsonDataCopy<D extends DidoData> {

    private final LinkedList<DataSchema> stack = new LinkedList<>();

    private final DataFactory<D> builder;

    private JsonDataCopy(DataSchema schema,
                         DataFactory<D> dataBuilder) {
        this.stack.addFirst(schema);
        this.builder = dataBuilder;
    }

    public static GsonBuilder registerSchema(GsonBuilder gsonBuilder,
                                             DataSchema schema) {
        return registerSchema(gsonBuilder, schema, new MapDataDataFactoryProvider().provideFactory(schema));
    }

    public static <D extends DidoData> GsonBuilder registerSchema(GsonBuilder gsonBuilder,
                                                                  DataSchema schema,
                                                                  DataFactory<D> dataBuilder) {
        return new JsonDataCopy<>(schema, dataBuilder).init(gsonBuilder, dataBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder, DataFactory<D> dataBuilder) {
        return gsonBuilder.registerTypeAdapter(dataBuilder.getDataType(),
                        new DataDeserializer())
                .registerTypeAdapter(RepeatingData.class, new RepeatingDeserializer());
    }

    class DataDeserializer implements JsonDeserializer<DidoData> {

        @Override
        public DidoData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject()) {
                throw new JsonParseException("JsonObject expected, received: " + json +
                        " (" + json.getClass().getSimpleName() + ")");
            }

            DataSetter setter = builder.getSetter();

            DataSchema schema = stack.getFirst();

            JsonObject jsonObject = (JsonObject) json;

            for (SchemaField schemaField : schema.getSchemaFields()) {

                String field = schemaField.getName();

                JsonElement element = jsonObject.get(field);

                if (element == null) {
                    continue;
                }

                Class<?> fieldType = schemaField.getType();
                if (schemaField.isNested()) {

                    stack.addFirst(schemaField.getNestedSchema());
                    fieldType = builder.getDataType();
                }

                Object nested = context.deserialize(element, fieldType);
                setter.setNamed(field, nested);

                if (schemaField.isNested()) {

                    stack.removeFirst();
                }
            }

            return builder.toData();
        }
    }

}
