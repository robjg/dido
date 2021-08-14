package dido.json;

import com.google.gson.*;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provide a wrapper around a {@link JsonObject} so that it can be accessed as an {@link GenericData}.
 */
public class JsonDataWrapper {

    private static final Map<Class<?>, GetterFactory> getterFactories = new HashMap<>();

    static {
        getterFactories.put(String.class, new StringGetterFactory());
        getterFactories.put(boolean.class, new BooleanGetterFactory());
        getterFactories.put(Boolean.class, new BooleanGetterFactory());
        getterFactories.put(byte.class, new ByteGetterFactory());
        getterFactories.put(Byte.class, new ByteGetterFactory());
        getterFactories.put(short.class, new ShortGetterFactory());
        getterFactories.put(Short.class, new ShortGetterFactory());
        getterFactories.put(int.class, new IntGetterFactory());
        getterFactories.put(Integer.class, new IntGetterFactory());
        getterFactories.put(long.class, new LongGetterFactory());
        getterFactories.put(Long.class, new LongGetterFactory());
        getterFactories.put(float.class, new FloatGetterFactory());
        getterFactories.put(Float.class, new FloatGetterFactory());
        getterFactories.put(double.class, new DoubleGetterFactory());
        getterFactories.put(Double.class, new DoubleGetterFactory());
        getterFactories.put(Number.class, new NumberGetterFactory());
    }


    private final Getter[] getters;

    private final DataSchema<String> schema;

    private JsonDataWrapper(Getter[] getters, DataSchema<String> schema) {
        this.getters = getters;
        this.schema = schema;
    }

    public static JsonDataWrapper from(DataSchema<String> schema) {

        Getter[] getters = new Getter[schema.lastIndex() + 1];

        for (String field : schema.getFields()) {

            Class<?> fieldType = schema.getType(field);
            GetterFactory getterFactory = getterFactories.get(fieldType);
            Getter getter;

            if (getterFactory == null) {
                if (fieldType.isArray()) {
                    getter = new ArrayGetter<>(field, fieldType);
                }
                else {
                    getter = new ObjectGetter<>(field, fieldType);
                }
            }
            else {
                getter = getterFactory.getForField(field);
            }

            int recIndex = schema.getIndex(field);

            getters[recIndex] = getter;
        }

        return new JsonDataWrapper(getters, schema);
    }

    public GenericData<String> wrap(JsonObject jsonObject, JsonDeserializationContext context) {

        return new GenericData<String>() {

            volatile int hash = 0;

            @Override
            public DataSchema<String> getSchema() {
                return schema;
            }

            @Override
            public <T> T getObjectAt(int index, Class<T> type) {
                Getter getter = getters[index];
                if (getter == null) {
                    throw new IllegalArgumentException("No field defined at " + index);
                }
                else {
                    if (getter instanceof NestedGetter) {
                        return (T) ((NestedGetter) getter).get(jsonObject, context);
                    }
                    else {
                        try {
                            return (T) getter.getClass().getMethod("get", JsonObject.class).invoke(getter, jsonObject);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            throw new IllegalStateException("Shouldn't happen.", e);
                        }
                    }
                }
            }

            @Override
            public String getStringAt(int index) {

                return ((StringGetter) getters[index]).get(jsonObject);
            }

            @Override
            public boolean hasIndex(int index) {
                return getters[index] != null;
            }

            @Override
            public boolean getBooleanAt(int index) {
                return ((BooleanGetter) getters[index]).get(jsonObject);
            }

            @Override
            public byte getByteAt(int index) {
                return ((ByteGetter) getters[index]).get(jsonObject);
            }

            @Override
            public short getShortAt(int index) {
                return ((ShortGetter) getters[index]).get(jsonObject);
            }

            @Override
            public int getIntAt(int index) {
                return ((IntGetter) getters[index]).get(jsonObject);
            }

            @Override
            public long getLongAt(int index) {
                return ((LongGetter) getters[index]).get(jsonObject);
            }

            @Override
            public float getFloatAt(int index) {
                return ((FloatGetter) getters[index]).get(jsonObject);
            }

            @Override
            public double getDoubleAt(int index) {
                return ((DoubleGetter) getters[index]).get(jsonObject);
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof IndexedData) {
                    return IndexedData.equals(this, (IndexedData<?>) o);

                }
                else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                if (hash == 0) {
                    hash = IndexedData.hashCode(this);
                }
                return hash;
            }

            @Override
            public String toString() {
                return IndexedData.toString(this);
            }
        };
    }

    interface Getter {

    }

    interface GetterFactory {

        Getter getForField(String field);
    }

    interface NestedGetter<T> extends Getter {
        T get(JsonObject jsonObject, JsonDeserializationContext context);
    }

    static class ObjectGetter<T> implements NestedGetter<T> {

        private final String field;

        private final Class<T> type;

        ObjectGetter(String field, Class<T> type) {
            this.field = field;
            this.type = type;
        }

        @Override
        public T get(JsonObject jsonObject, JsonDeserializationContext context) {
            JsonElement element = jsonObject.getAsJsonObject(field);
            if (element == null) {
                return null;
            }

            return context.deserialize(element, type);
        }
    }

    static class ArrayGetter<T> implements NestedGetter<T> {

        private final String field;

        private final Class<T> type;

        ArrayGetter(String field, Class<T> type) {
            this.field = field;
            this.type = type;
        }

        @Override
        public T get(JsonObject jsonObject, JsonDeserializationContext context) {
            JsonArray jsonArray = jsonObject.getAsJsonArray(field);

            if (jsonArray == null) {
                return null;
            }

            Object[] array = new Object[jsonArray.size()];

            int index = 0;
            for (JsonElement child : jsonArray) {
                array[index++] = context.deserialize(child, type.getComponentType());
            }

            return (T) array;
        }
    }

    static class StringGetter implements Getter {

        private final String field;

        StringGetter(String field) {
            this.field = field;
        }

        public String get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return null;
            }
            else {
                return jsonPrimitive.getAsString();
            }
        }
    }

    static class StringGetterFactory implements GetterFactory {

        @Override
        public Getter getForField(String field) {
            return new StringGetter(field);
        }
    }

    static class BooleanGetter implements Getter {

        private final String field;

        BooleanGetter(String field) {
            this.field = field;
        }

        public boolean get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return false;
            }
            else {
                return jsonPrimitive.getAsBoolean();
            }
        }
    }

    static class BooleanGetterFactory implements GetterFactory {

        @Override
        public BooleanGetter getForField(String field) {
            return new BooleanGetter(field);
        }
    }

    static class ByteGetter implements Getter {

        private final String field;

        ByteGetter(String field) {
            this.field = field;
        }

        public byte get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return (byte) 0;
            }
            else {
                return jsonPrimitive.getAsByte();
            }
        }
    }

    static class ByteGetterFactory implements GetterFactory {

        @Override
        public Getter getForField(String field) {
            return new ByteGetter(field);
        }
    }

    static class ShortGetter implements Getter {

        private final String field;

        ShortGetter(String field) {
            this.field = field;
        }

        public short get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return (short) 0;
            }
            else {
                return jsonPrimitive.getAsShort();
            }
        }
    }

    static class ShortGetterFactory implements GetterFactory {

        @Override
        public Getter getForField(String field) {
            return new ShortGetter(field);
        }
    }

    static class IntGetter implements Getter {

        private final String field;

        IntGetter(String field) {
            this.field = field;
        }

        public int get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return  0;
            }
            else {
                return jsonPrimitive.getAsInt();
            }
        }
    }

    static class IntGetterFactory implements GetterFactory {

        @Override
        public Getter getForField(String field) {
            return new IntGetter(field);
        }
    }

    static class LongGetter implements Getter {

        private final String field;

        LongGetter(String field) {
            this.field = field;
        }

        public long get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return 0L;
            }
            else {
                return jsonPrimitive.getAsLong();
            }
        }
    }

    static class LongGetterFactory implements GetterFactory {

        @Override
        public Getter getForField(String field) {
            return new LongGetter(field);
        }
    }

    static class FloatGetter implements Getter {

        private final String field;

        FloatGetter(String field) {
            this.field = field;
        }

        public float get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return 0.0F;
            }
            else {
                return jsonPrimitive.getAsFloat();
            }
        }
    }

    static class FloatGetterFactory implements GetterFactory {

        @Override
        public Getter getForField(String field) {
            return new FloatGetter(field);
        }
    }

    static class DoubleGetter implements Getter {

        private final String field;

        DoubleGetter(String field) {
            this.field = field;
        }

        public double get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return 0.0;
            }
            else {
                return jsonPrimitive.getAsDouble();
            }
        }
    }

    static class DoubleGetterFactory implements GetterFactory {

        @Override
        public Getter getForField(String field) {
            return new DoubleGetter(field);
        }
    }

    static class NumberGetter implements Getter {

        private final String field;

        NumberGetter(String field) {
            this.field = field;
        }

        public Number get(JsonObject jsonObject) {
            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(field);
            if (jsonPrimitive == null) {
                return null;
            }
            else {
                return jsonPrimitive.getAsNumber();
            }
        }
    }

    static class NumberGetterFactory implements GetterFactory {

        @Override
        public Getter getForField(String field) {
            return new NumberGetter(field);
        }
    }
}
