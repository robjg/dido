package dido.json;

import com.google.gson.*;
import dido.data.*;
import dido.how.util.Primitives;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Gson serializer for {@link DidoData}s.
 */
public class DataSerializer implements JsonSerializer<DidoData> {

    private final JsonSetter[] jsonSetters;

    private DataSerializer(JsonSetter[] jsonSetters) {
        this.jsonSetters = jsonSetters;
    }

    static class SerializerConstructor {

        private final Map<DataSchema, AtomicReference<DataSerializer>>
                serializers = new HashMap<>();

        private final Map<DataSchema, List<Consumer<DataSerializer>>> deferredActions =
                new HashMap<>();

        DataSerializer serializerFor(DataSchema schema) {

            AtomicReference<DataSerializer> ref = serializers.get(schema);
            if (ref == null) {
                serializers.put(schema, new AtomicReference<>(null));
            } else {
                return ref.get();
            }

            JsonSetter[] jsonSetters = new JsonSetter[schema.lastIndex()];

            ReadStrategy readStrategy = ReadStrategy.fromSchema(schema);

            for (SchemaField schemaField : schema.getSchemaFields()) {
                Type cl = Primitives.wrap(schemaField.getType());
                String name = schemaField.getName();
                FieldGetter getter = readStrategy.getFieldGetterNamed(name);
                int arrayIndex = schemaField.getIndex() - 1;
                if (Integer.class == cl) {
                    jsonSetters[arrayIndex] = new IntSetter(name, getter);
                } else if (Double.class == cl) {
                    jsonSetters[arrayIndex] = new DoubleSetter(name, getter);
                } else if (schemaField.isNested()) {
                    DataSchema nestedSchema = schemaField.getNestedSchema();
                    Consumer<DataSerializer> serializerConsumer = nestedSerializerNow -> {
                        if (schemaField.isRepeating()) {
                            jsonSetters[arrayIndex] = new RepeatingSetter(name, getter, nestedSerializerNow);
                        } else {
                            jsonSetters[arrayIndex] = new NestedSetter(name, getter, nestedSerializerNow);
                        }
                    };
                    DataSerializer nestedSerializer =
                            serializerFor(nestedSchema);
                    if (nestedSerializer == null) {
                        deferredActions.computeIfAbsent(nestedSchema, key -> new ArrayList<>())
                                .add(serializerConsumer);
                    } else {
                        serializerConsumer.accept(nestedSerializer);
                    }
                } else {
                    jsonSetters[arrayIndex] = new ObjectSetter(name, getter);
                }
            }

            DataSerializer serializerNow = new DataSerializer(jsonSetters);
            serializers.put(schema, new AtomicReference<>(serializerNow));
            List<Consumer<DataSerializer>> deferred = deferredActions.remove(schema);
            if (deferred != null) {
                deferred.forEach(c -> c.accept(serializerNow));
            }
            return serializerNow;
        }
    }


    public static JsonSerializer<DidoData> forSchema(DataSchema schema) {
        return new SerializerConstructor().serializerFor((schema));
    }

    public static JsonSerializer<DidoData> forUnknownSchema() {
        return new DataSerializer.Unknown();
    }

    @Override
    public JsonObject serialize(DidoData src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        for (JsonSetter jsonSetter : jsonSetters) {

            if (jsonSetter != null) {
                jsonSetter.doSet(jsonObject, src, context);
            }
        }

        return jsonObject;
    }

    static abstract class JsonSetter {

        final String name;

        final FieldGetter getter;

        JsonSetter(String name, FieldGetter getter) {
            this.name = name;
            this.getter = getter;
        }

        abstract void doSet(JsonObject jsonObject, DidoData data, JsonSerializationContext context);
    }

    static class NestedSetter extends JsonSetter {

        private final DataSerializer nestedSerializer;

        NestedSetter(String name, FieldGetter getter,
                     DataSerializer nestedSerializer) {
            super(name, getter);
            this.nestedSerializer = nestedSerializer;
        }

        @Override
        void doSet(JsonObject jsonObject, DidoData data, JsonSerializationContext context) {

            if (!getter.has(data)) {
                return;
            }

            DidoData nested = (DidoData) getter.get(data);

            JsonObject object = nestedSerializer.serialize(
                    nested, DidoData.class, context);

            jsonObject.add(name, object);
        }

    }

    static class RepeatingSetter extends JsonSetter {

        private final DataSerializer nestedSerializer;

        RepeatingSetter(String name, FieldGetter getter,
                        DataSerializer nestedSerializer) {
            super(name, getter);
            this.nestedSerializer = nestedSerializer;
        }

        @Override
        void doSet(JsonObject jsonObject, DidoData data, JsonSerializationContext context) {

            if (!getter.has(data)) {
                return;
            }

            JsonArray jsonArray = new JsonArray();

            RepeatingData src = (RepeatingData) getter.get(data);

            for (DidoData element : src) {

                JsonObject object = nestedSerializer.serialize(
                        element, DidoData.class, context);

                jsonArray.add(object);
            }

            jsonObject.add(name, jsonArray);
        }
    }

    static class ObjectSetter extends JsonSetter {

        ObjectSetter(String name, FieldGetter getter) {
            super(name, getter);
        }

        @Override
        public void doSet(JsonObject jsonObject, DidoData data, JsonSerializationContext context) {

            jsonObject.add(name, context.serialize(getter.get(data)));
        }
    }

    static class IntSetter extends JsonSetter {

        IntSetter(String name, FieldGetter getter) {
            super(name, getter);
        }

        @Override
        public void doSet(JsonObject jsonObject, DidoData data, JsonSerializationContext context) {

            if (getter.has(data)) {
                jsonObject.addProperty(name, getter.getInt(data));
            }
        }
    }

    static class DoubleSetter extends JsonSetter {

        DoubleSetter(String name, FieldGetter getter) {
            super(name, getter);
        }

        @Override
        public void doSet(JsonObject jsonObject, DidoData data, JsonSerializationContext context) {

            if (getter.has(data)) {
                jsonObject.addProperty(name, getter.getDouble(data));
            }
        }
    }

    static class Unknown implements JsonSerializer<DidoData> {

        private final SerializerConstructor serializerConstructor = new SerializerConstructor();

        private DataSchema lastSchema;
        private DataSerializer serializer;

        @Override
        public JsonElement serialize(DidoData src, Type typeOfSrc, JsonSerializationContext context) {

            DataSchema schema = src.getSchema();
            if (!schema.equals(lastSchema)) {
                lastSchema = schema;
                serializer = serializerConstructor.serializerFor(schema);
            }

            return serializer.serialize(src, typeOfSrc, context);
        }
    }
}
