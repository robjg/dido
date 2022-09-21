package dido.json;

import com.google.gson.*;
import dido.data.*;

import java.lang.reflect.Type;
import java.util.*;

public class JsonDataPartialCopy {

    private final LinkedList<DataSchema<String>> stack = new LinkedList<>();

    private JsonDataPartialCopy(DataSchema<String> partialSchema) {
        this.stack.addFirst(partialSchema);
    }

    public static GsonBuilder registerNoSchema(GsonBuilder gsonBuilder) {
        return new JsonDataPartialCopy(DataSchema.emptySchema()).init(gsonBuilder);
    }

    public static GsonBuilder registerPartialSchema(GsonBuilder gsonBuilder,
                                                    DataSchema<String> partialSchema) {
        return new JsonDataPartialCopy(partialSchema == null ? DataSchema.emptySchema() : partialSchema)
                .init(gsonBuilder);
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

            DataSchema<String> prioritySchema = stack.getFirst();
            SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();
            Set<String> knownFields = new HashSet<>(prioritySchema.getFields());

            JsonObject jsonObject = (JsonObject) json;

            Object[] values = new Object[jsonObject.size()];
            int index = 0;

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

                String field = entry.getKey();

                JsonElement element = entry.getValue();

                if (knownFields.contains(field)) {

                    SchemaField<String> schemaField = prioritySchema.getSchemaField(field);

                    if (schemaField.isNested()) {

                        stack.addFirst(schemaField.getNestedSchema());
                    }

                    Object value = context.deserialize(element, schemaField.getType());
                    values[index] = value;

                    SchemaField<String> childField;
                    if (schemaField.isNested()) {

                        stack.removeFirst();
                        if (schemaField.isRepeating()) {
                            RepeatingData<String> repeatingData = (RepeatingData<String>) value;
                            if (repeatingData.size() > 0) {
                                childField = SchemaField.ofRepeating(++index, field,
                                        repeatingData.get(0).getSchema());
                            } else {
                                childField = schemaField.mapToIndex(++index);
                            }
                        } else {
                            childField = SchemaField.ofNested(++index, field, ((IndexedData<String>) value).getSchema());
                        }
                    }
                    else {
                        childField = SchemaField.of(++index, field, schemaField.getType());
                    }
                    schemaBuilder.addSchemaField(childField);

                } else {

                    Object value = context.deserialize(element, Object.class);

                    Class<?> type = value.getClass();

                    values[index] = value;
                    schemaBuilder.addFieldAt(++index, field, type);
                }
            }

            return ArrayData.valuesFor(schemaBuilder.build()).of(values);
        }
    }

}
