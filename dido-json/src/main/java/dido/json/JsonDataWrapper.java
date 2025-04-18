package dido.json;

import com.google.gson.*;
import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.useful.AbstractData;
import dido.data.useful.AbstractFieldGetter;
import dido.data.useful.DataSchemaImpl;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Provide a wrapper around a {@link JsonObject} so that it can be accessed as an {@link DidoData}.
 */
public class JsonDataWrapper {

    private static final Object NONE = new Object();

    private final DataDeserializer deserializer;

    private JsonDataWrapper(DataSchema schema) {
        this.deserializer = new DataDeserializer(schema);
    }

    public static GsonBuilder registerSchema(GsonBuilder gsonBuilder,
                                             DataSchema schema) {
        return new JsonDataWrapper(schema).init(gsonBuilder);
    }

    private GsonBuilder init(GsonBuilder gsonBuilder) {
        return gsonBuilder
                .registerTypeAdapter(SchemaField.NESTED_TYPE, deserializer)
                .registerTypeAdapter(SchemaField.NESTED_REPEATING_TYPE, new RepeatingDeserializer());
    }

    class DataDeserializer implements JsonDeserializer<DidoData> {

        private Schema schema;

        DataDeserializer(DataSchema schema) {
            setSchema(schema);
        }

        void setSchema(DataSchema schema) {
            this.schema = Objects.requireNonNull(schema) instanceof DataSchemaImpl ?
                    new Schema((DataSchemaImpl) schema) :
                    new Schema(schema.getSchemaFields(), schema.firstIndex(), schema.lastIndex());
        }

        @Override
        public DidoData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            return wrap((JsonObject) json, context);
        }
    }

    public DidoData wrap(JsonObject jsonObject, JsonDeserializationContext context) {

        final Schema schema = deserializer.schema;

        final Object[] values = new Object[schema.lastIndex()];

        return new AbstractData() {

            @Override
            public ReadSchema getSchema() {
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
                if (element == null || element.isJsonNull()) {
                    values[index - 1] = NONE;
                    return null;
                }

                SchemaField schemaField = schema.getSchemaFieldAt(index);

                Type fieldType = schemaField.getType();

                Schema restore = null;
                if (schemaField.isNested()) {
                    restore = deserializer.schema;
                    deserializer.setSchema(schemaField.getNestedSchema());
                    if (schemaField.isRepeating()) {
                        fieldType = SchemaField.NESTED_REPEATING_TYPE;
                    } else {
                        fieldType = SchemaField.NESTED_TYPE;
                    }
                }

                value = context.deserialize(element, fieldType);
                values[index - 1] = value;

                if (restore != null) {
                    deserializer.schema = restore;
                }

                return value;
            }

            @Override
            public boolean hasAt(int index) {
                Object value = values[index - 1];
                if (value == null) {
                    return getAt(index) != null;
                } else {
                    return value != NONE;
                }
            }
        };
    }

    static class Schema extends DataSchemaImpl implements ReadSchema {

        Schema(DataSchemaImpl schema) {
            super(schema);
        }

        Schema(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {
            super(schemaFields, firstIndex, lastIndex);
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            return new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    return data.getAt(index);
                }
            };
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, Schema.this);
            }
            return getFieldGetterAt(index);
        }
    }
}
