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

    private DataSchema<String> prioritySchema;

    private JsonSchemaExtractor(DataSchema<String> prioritySchema) {
        this.prioritySchema = Objects.requireNonNull(prioritySchema);
    }

    public static JsonSchemaExtractor withPartialSchema(DataSchema<String> schema) {
        return new JsonSchemaExtractor(schema);
    }

    public static JsonSchemaExtractor withNoSchema() {
        return withPartialSchema(DataSchema.emptySchema());
    }


    public static GsonBuilder registerNoSchema(GsonBuilder gsonBuilder) {
        return new JsonSchemaExtractor(DataSchema.emptySchema()).init(gsonBuilder);
    }

    public static GsonBuilder registerPartialSchema(GsonBuilder gsonBuilder,
                                                    DataSchema<String> partialSchema) {
        return new JsonSchemaExtractor(partialSchema).init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(DataSchema.class, new SchemaDeserializer())
                .registerTypeAdapter(RepeatingSchema.class, new RepeatingSchemaDeserializer());
    }

    public DataSchema<String> fromString(String json) throws JsonParseException {

        Gson gson = init(new GsonBuilder()).create();

        return gson.fromJson(json, DataSchema.class);
    }

    public DataSchema<String> fromElement(JsonObject jsonObject) throws JsonParseException {

        Gson gson = init(new GsonBuilder()).create();

        return gson.fromJson(jsonObject, DataSchema.class);
    }

    protected void processPrimitive(SchemaBuilder<String> schemaBuilder,
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

    protected void processArray(SchemaBuilder<String> schemaBuilder,
                                String field,
                                JsonArray jsonArray) {

        if (jsonArray.size() > 0) {

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

    class SchemaDeserializer implements JsonDeserializer<DataSchema<String>> {

        @Override
        public DataSchema<String> deserialize(JsonElement json,
                                              Type typeOfT,
                                              JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();

            DataSchema<String> prioritySchema = JsonSchemaExtractor.this.prioritySchema;
            SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

            LinkedList<String> schemaFields = new LinkedList<>(prioritySchema.getFields());

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

                String field = entry.getKey();
                JsonElement element = entry.getValue();

                SchemaField<String> schemaField = prioritySchema.getSchemaField(field);

                if (schemaField == null) {

                    if (element.isJsonPrimitive()) {

                        processPrimitive(schemaBuilder, field,
                                element.getAsJsonPrimitive());
                    } else if (element.isJsonArray()) {

                        processArray(schemaBuilder, field,
                                element.getAsJsonArray());
                    } else {

                        schemaBuilder.addField(field, Map.class);
                    }

                } else {

                    if (schemaField.isNested()) {

                        JsonSchemaExtractor.this.prioritySchema = schemaField.getNestedSchema();

                        if (schemaField.isRepeating()) {

                            RepeatingSchema repeatingSchema = context.deserialize(element, RepeatingSchema.class);

                            schemaBuilder.addRepeatingField(field, repeatingSchema.nestedSchema);
                        } else {

                            DataSchema<String> nestedSchema = context.deserialize(element, DataSchema.class);

                            schemaBuilder.addNestedField(field, nestedSchema);
                        }
                    } else {

                        schemaBuilder.addField(field, schemaField.getType());
                    }
                }

                schemaFields.remove(field);
            }

            schemaFields.forEach(f -> schemaBuilder.addField(f, prioritySchema.getType(f)));

            return schemaBuilder.build();
        }
    }

    static class RepeatingSchema {

        private final DataSchema<String> nestedSchema;

        RepeatingSchema(DataSchema<String> nestedSchema) {
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

            if (jsonArray.size() == 0) {
                return new RepeatingSchema(JsonSchemaExtractor.this.prioritySchema);

            } else {
                DataSchema<String> nested = context.deserialize(
                        jsonArray.get(0), DataSchema.class);

                return new RepeatingSchema(nested);
            }
        }
    }
}