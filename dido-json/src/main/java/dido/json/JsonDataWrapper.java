package dido.json;

import com.google.gson.*;
import dido.data.*;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Provide a wrapper around a {@link JsonObject} so that it can be accessed as an {@link DidoData}.
 */
public class JsonDataWrapper {

    private static final Object NONE = new Object();

    private final DataSchema schema;

    private JsonDataWrapper(DataSchema schema) {
        this.schema = schema;
    }


    public static GsonBuilder registerSchema(GsonBuilder gsonBuilder,
                                             DataSchema schema) {
        return new JsonDataWrapper(schema).init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        deserializer = new DataDeserializer(schema);
        return gsonBuilder.registerTypeAdapter(DidoData.class, deserializer)
                .registerTypeAdapter(RepeatingData.class, new RepeatingDeserializer());
    }

    DataDeserializer deserializer;

    class DataDeserializer implements JsonDeserializer<DidoData> {

        private DataSchema schema;

        DataDeserializer(DataSchema schema) {
            this.schema = schema;
        }

        @Override
        public DidoData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            return wrap((JsonObject) json, context);
        }
    }

    public DidoData wrap(JsonObject jsonObject, JsonDeserializationContext context) {

        final DataSchema schema = Objects.requireNonNull(deserializer.schema);

        final Object[] values = new Object[schema.lastIndex()];


        return new AbstractData() {

            @Override
            public DataSchema getSchema() {
                return schema;
            }

            @Override
            public Object getAt(int index) {

                Object value = values[index - 1];

                if (value == NONE) {
                    return null;
                } else if (value != null) {
                    return value;
                }

                JsonElement element = jsonObject.get(schema.getFieldNameAt(index));
                if (element == null) {
                    values[index - 1] = NONE;
                    return null;
                }

                SchemaField schemaField = schema.getSchemaFieldAt(index);

                DataSchema restore = null;
                if (schemaField.isNested()) {
                    restore = deserializer.schema;
                    deserializer.schema = schemaField.getNestedSchema();
                }

                value = context.deserialize(element, schemaField.getType());
                values[index - 1] = value;

                if (restore != null) {
                    deserializer.schema = restore;
                }

                return value;
            }

            @Override
            public <T> T getAs(String fieldName, Class<T> type) {
                int index = getSchema().getIndexNamed(fieldName);
                if (index > 0) {
                    return getAtAs(index, type);
                } else {
                    return null;
                }
            }

            @Override
            public boolean hasIndex(int index) {
                Object value = values[index - 1];
                if (value == null) {
                    return getAt(index) != null;
                }
                else {
                    return value != NONE;
                }
            }

            @Override
            public String toString() {
                return DidoData.toStringFieldsOnly(this);
            }
        };
    }
}
