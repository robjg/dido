package dido.json;

import com.google.gson.*;
import dido.data.*;
import dido.data.util.FieldValuesIn;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param <D>
 */
public class JsonDataPartialCopy<D extends DidoData> {

    private final DataFactoryProvider<D> dataFactoryProvider;

    private final LinkedList<DataSchema> stack = new LinkedList<>();

    private JsonDataPartialCopy(DataSchema partialSchema,
                                DataFactoryProvider<D> dataFactoryProvider) {
        this.stack.addFirst(partialSchema);
        this.dataFactoryProvider = dataFactoryProvider;
    }

    public static <D extends DidoData> GsonBuilder registerNoSchema(GsonBuilder gsonBuilder,
                                                                    DataFactoryProvider<D> dataFactory) {
        return new JsonDataPartialCopy<>(DataSchema.emptySchema(), dataFactory).init(gsonBuilder);
    }

    public static <D extends DidoData> GsonBuilder registerPartialSchema(GsonBuilder gsonBuilder,
                                                                         DataSchema partialSchema,
                                                                         DataFactoryProvider<D> dataFactory) {
        return new JsonDataPartialCopy<>(partialSchema == null ? DataSchema.emptySchema() : partialSchema,
                dataFactory).init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        return gsonBuilder
                .registerTypeAdapter(dataFactoryProvider.getDataType(),
                        new DataDeserializer())
                .registerTypeAdapter(SchemaField.NESTED_REPEATING_TYPE,
                        new RepeatingDeserializer(dataFactoryProvider.getDataType()));
    }

    class DataDeserializer implements JsonDeserializer<D> {


        @Override
        public D deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject()) {
                throw new JsonParseException("JsonObject expected, received: " + json +
                        " (" + json.getClass().getSimpleName() + ")");
            }

            DataSchema prioritySchema = stack.getFirst();
            SchemaFactory schemaFactory = dataFactoryProvider.getSchemaFactory();
            Set<String> knownFields = new HashSet<>(prioritySchema.getFieldNames());

            JsonObject jsonObject = (JsonObject) json;

            Object[] values = new Object[jsonObject.size()];
            int index = 0;

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

                String fieldName = entry.getKey();

                JsonElement element = entry.getValue();

                if (knownFields.contains(fieldName)) {

                    SchemaField schemaField = prioritySchema.getSchemaFieldNamed(fieldName);
                    Type fieldType = schemaField.getType();

                    if (schemaField.isNested()) {

                        stack.addFirst(schemaField.getNestedSchema());
                        if (schemaField.isRepeating()) {
                            fieldType = SchemaField.NESTED_REPEATING_TYPE;
                        }
                        else {
                            fieldType = dataFactoryProvider.getDataType();
                        }
                    }

                    Object value = context.deserialize(element, fieldType);
                    values[index] = value;

                    SchemaField childField;
                    if (schemaField.isNested()) {

                        stack.removeFirst();
                        if (schemaField.isRepeating()) {
                            RepeatingData repeatingData = (RepeatingData) value;
                            if (repeatingData.size() > 0) {
                                childField = SchemaField.ofRepeating(++index, fieldName,
                                        repeatingData.get(0).getSchema());
                            } else {
                                childField = schemaField.mapToIndex(++index);
                            }
                        } else {
                            childField = SchemaField.ofNested(++index, fieldName, ((DidoData) value).getSchema());
                        }
                    } else {
                        childField = SchemaField.of(++index, fieldName, schemaField.getType());
                    }
                    schemaFactory.addSchemaField(childField);

                } else {

                    Object value = context.deserialize(element, Object.class);

                    Class<?> type = value.getClass();

                    values[index] = value;
                    schemaFactory.addSchemaField(SchemaField.of(++index, fieldName, type));
                }
            }

            DataSchema schema = schemaFactory.toSchema();

            return FieldValuesIn.withDataFactory(dataFactoryProvider.provideFactory(schema))
                    .of(values);
        }
    }
}
