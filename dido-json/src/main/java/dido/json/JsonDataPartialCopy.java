package dido.json;

import com.google.gson.*;
import dido.data.*;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class JsonDataPartialCopy {

    private final LinkedList<DataSchema> stack = new LinkedList<>();

    private JsonDataPartialCopy(DataSchema partialSchema) {
        this.stack.addFirst(partialSchema);
    }

    public static GsonBuilder registerNoSchema(GsonBuilder gsonBuilder) {
        return new JsonDataPartialCopy(DataSchema.emptySchema()).init(gsonBuilder);
    }

    public static GsonBuilder registerPartialSchema(GsonBuilder gsonBuilder,
                                                    DataSchema partialSchema) {
        return new JsonDataPartialCopy(partialSchema == null ? DataSchema.emptySchema() : partialSchema)
                .init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(DidoData.class,
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

            DataSchema prioritySchema = stack.getFirst();
            SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();
            Set<String> knownFields = new HashSet<>(prioritySchema.getFieldNames());

            JsonObject jsonObject = (JsonObject) json;

            Object[] values = new Object[jsonObject.size()];
            int index = 0;

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

                String fieldName = entry.getKey();

                JsonElement element = entry.getValue();

                if (knownFields.contains(fieldName)) {

                    SchemaField schemaField = prioritySchema.getSchemaFieldNamed(fieldName);

                    if (schemaField.isNested()) {

                        stack.addFirst(schemaField.getNestedSchema());
                    }

                    Object value = context.deserialize(element, schemaField.getType());
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
                    }
                    else {
                        childField = SchemaField.of(++index, fieldName, schemaField.getType());
                    }
                    schemaBuilder.addSchemaField(childField);

                } else {

                    Object value = context.deserialize(element, Object.class);

                    Class<?> type = value.getClass();

                    values[index] = value;
                    schemaBuilder.addFieldAt(++index, fieldName, type);
                }
            }

            return ArrayData.valuesFor(schemaBuilder.build()).of(values);
        }
    }

}
