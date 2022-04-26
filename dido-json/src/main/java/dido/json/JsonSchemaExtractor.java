package dido.json;

import com.google.gson.*;
import dido.data.DataSchema;
import dido.data.SchemaBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Extract the schema from JSON.
 */
public class JsonSchemaExtractor {

    private final DataSchema<String> prioritySchema;

    public JsonSchemaExtractor(DataSchema<String> prioritySchema) {
        this.prioritySchema = Objects.requireNonNull(prioritySchema);
    }

    public static JsonSchemaExtractor from(DataSchema<String> schema) {
        return new JsonSchemaExtractor(schema);
    }

    public DataSchema<String> fromElement(JsonObject jsonObject) throws JsonParseException {

        SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

        Collection<String> existingFields = prioritySchema.getFields();

        for (Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {

            String field = entry.getKey();
            if (existingFields.contains(field)) {

                // So the field from the priority schema is in the same place.
                schemaBuilder.addField(field, Void.class);

                continue;
            }

            JsonElement element = entry.getValue();

            if (element.isJsonPrimitive()) {

                JsonPrimitive jsonPrimitive = (JsonPrimitive) element;

                if (jsonPrimitive.isString()) {
                    schemaBuilder.addField(field, String.class);
                }
                else if (jsonPrimitive.isBoolean()) {
                    schemaBuilder.addField(field, boolean.class);
                }
                else {
                    schemaBuilder.addField(field, double.class);
                }
            }
            else if (element.isJsonArray()) {

                schemaBuilder.addField(field, Object[].class);
            }
            else {

                schemaBuilder.addField(field, Object.class);
            }
        }

        return schemaBuilder.merge(prioritySchema)
                .build();
    }
}
