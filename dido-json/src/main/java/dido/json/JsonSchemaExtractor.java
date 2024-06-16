package dido.json;

import com.google.gson.*;
import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.data.SchemaField;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * Extract the schema from JSON.
 */
public class JsonSchemaExtractor {

    private DataSchema prioritySchema;

    private JsonSchemaExtractor(DataSchema prioritySchema) {
        this.prioritySchema = Objects.requireNonNull(prioritySchema);
    }

    public static JsonSchemaExtractor withPartialSchema(DataSchema schema) {
        return new JsonSchemaExtractor(schema);
    }

    public static JsonSchemaExtractor withNoSchema() {
        return withPartialSchema(DataSchema.emptySchema());
    }


    public static GsonBuilder registerNoSchema(GsonBuilder gsonBuilder) {
        return new JsonSchemaExtractor(DataSchema.emptySchema()).init(gsonBuilder);
    }

    public static GsonBuilder registerPartialSchema(GsonBuilder gsonBuilder,
                                                    DataSchema partialSchema) {
        return new JsonSchemaExtractor(partialSchema).init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(DataSchema.class, new SchemaDeserializer())
                .registerTypeAdapter(RepeatingSchema.class, new RepeatingSchemaDeserializer());
    }

    public DataSchema fromString(String json) throws JsonParseException {

        Gson gson = init(new GsonBuilder()).create();

        return gson.fromJson(json, DataSchema.class);
    }

    public DataSchema fromElement(JsonObject jsonObject) throws JsonParseException {

        Gson gson = init(new GsonBuilder()).create();

        return gson.fromJson(jsonObject, DataSchema.class);
    }

    protected void processPrimitive(SchemaBuilder schemaBuilder,
                                    String field,
                                    JsonPrimitive jsonPrimitive) {

        if (jsonPrimitive.isString()) {
            schemaBuilder.addField(field, String.class);
        } else if (jsonPrimitive.isBoolean()) {
            schemaBuilder.addField(field, boolean.class);
        } else {
            schemaBuilder.addField(field, double.class);
        }
    }

    protected void processArray(SchemaBuilder schemaBuilder,
                                String field,
                                JsonArray jsonArray) {

        if (!jsonArray.isEmpty()) {

            JsonElement element = jsonArray.get(0);

            if (element.isJsonPrimitive()) {

                JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();

                if (jsonPrimitive.isString()) {
                    schemaBuilder.addField(field, String[].class);
                } else if (jsonPrimitive.isBoolean()) {
                    schemaBuilder.addField(field, boolean[].class);
                } else {
                    schemaBuilder.addField(field, double[].class);
                }
            } else if (element.isJsonArray()) {

                // Not sure what to do here.
                schemaBuilder.addField(field, Object[].class);
            } else {

                schemaBuilder.addField(field, Object[].class);
            }
        } else {
            schemaBuilder.addField(field, Object[].class);
        }
    }

    class SchemaDeserializer implements JsonDeserializer<DataSchema> {

        @Override
        public DataSchema deserialize(JsonElement json,
                                                     Type typeOfT,
                                                     JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();

            DataSchema prioritySchema = JsonSchemaExtractor.this.prioritySchema;
            SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

            LinkedList<String> fieldNames = new LinkedList<>(prioritySchema.getFieldNames());

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

                String fieldName = entry.getKey();
                JsonElement element = entry.getValue();

                SchemaField schemaField = prioritySchema.getSchemaFieldNamed(fieldName);

                if (schemaField == null) {

                    if (element.isJsonPrimitive()) {

                        processPrimitive(schemaBuilder, fieldName,
                                element.getAsJsonPrimitive());
                    } else if (element.isJsonArray()) {

                        processArray(schemaBuilder, fieldName,
                                element.getAsJsonArray());
                    } else {

                        schemaBuilder.addField(fieldName, Map.class);
                    }

                } else {

                    if (schemaField.isNested()) {

                        JsonSchemaExtractor.this.prioritySchema = schemaField.getNestedSchema();

                        if (schemaField.isRepeating()) {

                            RepeatingSchema repeatingSchema = context.deserialize(element, RepeatingSchema.class);

                            schemaBuilder.addRepeatingField(fieldName, repeatingSchema.nestedSchema);
                        } else {

                            DataSchema nestedSchema = context.deserialize(element, DataSchema.class);

                            schemaBuilder.addNestedField(fieldName, nestedSchema);
                        }
                    } else {

                        schemaBuilder.addField(fieldName, schemaField.getType());
                    }
                }

                fieldNames.remove(fieldName);
            }

            fieldNames.forEach(f -> schemaBuilder.addField(f, prioritySchema.getTypeNamed(f)));

            return schemaBuilder.build();
        }
    }

    static class RepeatingSchema {

        private final DataSchema nestedSchema;

        RepeatingSchema(DataSchema nestedSchema) {
            this.nestedSchema = nestedSchema;
        }
    }

    class RepeatingSchemaDeserializer implements JsonDeserializer<RepeatingSchema> {

        @Override
        public RepeatingSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonArray()) {
                throw new JsonParseException("JsonArray expected, received: " + json +
                        " (" + json.getClass().getSimpleName() + ")");
            }

            JsonArray jsonArray = json.getAsJsonArray();

            if (jsonArray.isEmpty()) {
                return new RepeatingSchema(JsonSchemaExtractor.this.prioritySchema);

            } else {
                DataSchema nested = context.deserialize(
                        jsonArray.get(0), DataSchema.class);

                return new RepeatingSchema(nested);
            }
        }
    }
}