package dido.json;

import com.google.gson.*;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.RepeatingData;
import dido.data.SchemaField;
import dido.data.mutable.MalleableArrayData;
import dido.data.mutable.MalleableData;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

/**
 * Reads JSON when the schema is unknown or partially known.
 */
public class JsonDataPartialCopy {

    private final Supplier<MalleableData> malleableDataSupplier;

    private final LinkedList<DataSchema> stack = new LinkedList<>();

    private JsonDataPartialCopy(DataSchema partialSchema,
                                Supplier<MalleableData> malleableDataSupplier) {
        this.stack.addFirst(partialSchema);
        this.malleableDataSupplier = Objects.requireNonNullElse(malleableDataSupplier,
                MalleableArrayData::new);
    }

    public static GsonBuilder registerNoSchema(GsonBuilder gsonBuilder,
                                               Supplier<MalleableData> dataFactory) {
        return new JsonDataPartialCopy(DataSchema.emptySchema(), dataFactory).init(gsonBuilder);
    }

    public static GsonBuilder registerPartialSchema(GsonBuilder gsonBuilder,
                                                    DataSchema partialSchema,
                                                    Supplier<MalleableData> dataFactory) {
        return new JsonDataPartialCopy(partialSchema == null ? DataSchema.emptySchema() : partialSchema,
                dataFactory).init(gsonBuilder);
    }

    public static GsonBuilder registerPartialSchema(GsonBuilder gsonBuilder,
                                                    DataSchema partialSchema) {
        return new JsonDataPartialCopy(
                partialSchema == null ? DataSchema.emptySchema() : partialSchema,
                null)
                .init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        return gsonBuilder
                .registerTypeAdapter(DidoData.class,
                        new DataDeserializer())
                .registerTypeAdapter(SchemaField.NESTED_REPEATING_TYPE,
                        new RepeatingDeserializer());
    }

    class DataDeserializer implements JsonDeserializer<DidoData> {


        @Override
        public DidoData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject()) {
                throw new JsonParseException("JsonObject expected, received: " + json +
                        " (" + json.getClass().getSimpleName() + ")");
            }

            DataSchema prioritySchema = stack.getFirst();
            Set<String> knownFields = new HashSet<>(prioritySchema.getFieldNames());

            JsonObject jsonObject = (JsonObject) json;

            MalleableData data = malleableDataSupplier.get();

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
                        } else {
                            fieldType = SchemaField.NESTED_TYPE;
                        }
                    }

                    Object value = context.deserialize(element, fieldType);

                    SchemaField newField;
                    if (schemaField.isNested()) {

                        stack.removeFirst();
                        if (schemaField.isRepeating()) {
                            RepeatingData repeatingData = (RepeatingData) value;
                            if (repeatingData.size() > 0) {
                                newField = SchemaField.ofRepeating(0, fieldName,
                                        repeatingData.get(0).getSchema());
                            } else {
                                newField = schemaField.mapToIndex(0);
                            }
                        } else {
                            newField = SchemaField.ofNested(0, fieldName, ((DidoData) value).getSchema());
                        }
                    } else {
                        newField = SchemaField.of(0, fieldName, schemaField.getType());
                    }

                    data.setField(newField, value);

                } else {

                    processJson(fieldName, element, data, context);
                }
            }

            return data;
        }
    }

    protected void processJson(String fieldName,
                               JsonElement element,
                               MalleableData data,
                               JsonDeserializationContext context) {

        if (element.isJsonPrimitive()) {

            processPrimitive(data, fieldName,
                    element.getAsJsonPrimitive());
        } else if (element.isJsonNull()) {

            data.setNamed(fieldName, null, void.class);

        } else if (element.isJsonArray()) {

            processArray(data, fieldName,
                    element.getAsJsonArray(),
                    context);
        } else {

            Object map = context.deserialize(element, Map.class);
            data.setNamed(fieldName, map, Map.class);
        }
    }

    protected void processPrimitive(MalleableData data,
                                    String field,
                                    JsonPrimitive jsonPrimitive) {

        if (jsonPrimitive.isString()) {
            data.setStringNamed(field, jsonPrimitive.getAsString());
        } else if (jsonPrimitive.isBoolean()) {
            data.setBooleanNamed(field, jsonPrimitive.getAsBoolean());
        } else {
            data.setDoubleNamed(field, jsonPrimitive.getAsDouble());
        }
    }

    protected void processArray(MalleableData data,
                                String field,
                                JsonArray jsonArray,
                                JsonDeserializationContext context) {
        Class<?> type = null;
        Object[] array = new Object[jsonArray.size()];

        int index = 0;
        for (JsonElement element : jsonArray.asList()) {

            if (element.isJsonPrimitive()) {

                JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();

                if (jsonPrimitive.isString()) {
                    array[index++] = jsonPrimitive.getAsString();
                    if (type == null) {
                        type = String.class;
                    } else if (type != String.class) {
                        type = Object.class;
                    }
                } else if (jsonPrimitive.isBoolean()) {
                    array[index++] = jsonPrimitive.getAsBoolean();
                    if (type == null) {
                        type = boolean.class;
                    } else if (type != boolean.class) {
                        type = Object.class;
                    }
                } else {
                    array[index++] = jsonPrimitive.getAsDouble();
                    if (type == null) {
                        type = double.class;
                    } else if (type != double.class) {
                        type = Object.class;
                    }
                }
            } else if (element.isJsonArray()) {

                Object[] na = context.deserialize(element, Object[].class);
                array[index++] = na;
                type = Object.class;
            } else {

                Object o = context.deserialize(element, Object.class);
                array[index++] = o;
                type = Object.class;
            }
        }

        Object result = array;
        if (type != null) {
            if (type == String.class) {
                String[] na = new String[array.length];
                for (int i = 0; i < array.length; ++i) {
                    na[i] = (String) array[i];
                }
                result = na;
            } else if (type == boolean.class) {
                boolean[] na = new boolean[array.length];
                for (int i = 0; i < array.length; ++i) {
                    na[i] = (boolean) array[i];
                }
                result = na;
            } else if (type == double.class) {
                double[] na = new double[array.length];
                for (int i = 0; i < array.length; ++i) {
                    na[i] = (double) array[i];
                }
                result = na;
            }
        }

        data.setNamed(field, result);
    }
}
