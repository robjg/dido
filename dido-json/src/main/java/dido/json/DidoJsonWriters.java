package dido.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dido.data.*;
import dido.how.util.Primitives;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Writes {@link DidoData} to a JsonWriter.
 */
public class DidoJsonWriters implements DidoJsonWriter {

    private final MiniWriter[] miniWriters;

    private DidoJsonWriters(MiniWriter[] miniWriters) {
        this.miniWriters = miniWriters;
    }

    static class WriterConstructor {

        // Cache writers by schema
        private final Map<DataSchema, AtomicReference<DidoJsonWriter>>
                serializers = new HashMap<>();

        // To unravel recursive schemas.
        private final Map<DataSchema, List<Consumer<DidoJsonWriter>>> deferredActions =
                new HashMap<>();

        private final Gson gson;

        WriterConstructor(Gson gson) {
            this.gson = gson;
        }

        DidoJsonWriter serializerFor(DataSchema schema) {

            AtomicReference<DidoJsonWriter> ref = serializers.get(schema);
            if (ref == null) {
                serializers.put(schema, new AtomicReference<>(null));
            } else {
                return ref.get();
            }

            MiniWriter[] miniWriters = new MiniWriter[schema.lastIndex()];

            ReadStrategy readStrategy = ReadStrategy.fromSchema(schema);

            for (SchemaField schemaField : schema.getSchemaFields()) {

                Type cl = Primitives.wrap(schemaField.getType());
                String name = schemaField.getName();
                FieldGetter getter = readStrategy.getFieldGetterNamed(name);
                int arrayIndex = schemaField.getIndex() - 1;

                if (String.class == cl) {
                    miniWriters[arrayIndex] = new StringWriter(name, getter);
                } else if (Boolean.class == cl) {
                    miniWriters[arrayIndex] = new BooleanWriter(name, getter);
                } else if (Integer.class == cl) {
                    miniWriters[arrayIndex] = new IntWriter(name, getter);
                } else if (Double.class == cl) {
                    miniWriters[arrayIndex] = new DoubleWriter(name, getter);
                } else if (schemaField.isNested()) {
                    DataSchema nestedSchema = schemaField.getNestedSchema();
                    Consumer<DidoJsonWriter> serializerConsumer = nestedSerializerNow -> {
                        if (schemaField.isRepeating()) {
                            miniWriters[arrayIndex] = new RepeatingWriter(name, getter, nestedSerializerNow);
                        } else {
                            miniWriters[arrayIndex] = new NestedWriter(name, getter, nestedSerializerNow);
                        }
                    };
                    DidoJsonWriter nestedSerializer =
                            serializerFor(nestedSchema);
                    if (nestedSerializer == null) {
                        deferredActions.computeIfAbsent(nestedSchema, key -> new ArrayList<>())
                                .add(serializerConsumer);
                    } else {
                        serializerConsumer.accept(nestedSerializer);
                    }
                } else {
                    miniWriters[arrayIndex] = new ObjectWriter(name, getter,
                            gson, cl);
                }
            }

            DidoJsonWriters serializerNow = new DidoJsonWriters(miniWriters);
            serializers.put(schema, new AtomicReference<>(serializerNow));
            List<Consumer<DidoJsonWriter>> deferred = deferredActions.remove(schema);
            if (deferred != null) {
                deferred.forEach(c -> c.accept(serializerNow));
            }
            return serializerNow;
        }
    }

    public static DidoJsonWriter forSchema(DataSchema schema, Gson gson) {
        return new WriterConstructor(gson).serializerFor((schema));
    }

    public static DidoJsonWriter forUnknownSchema(Gson gson) {
        return new DidoJsonWriters.Unknown(gson);
    }

    @Override
    public JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

        jsonWriter.beginObject();
        for (MiniWriter miniWriter : miniWriters) {
            if (miniWriter != null) {
                miniWriter.write(data, jsonWriter);
            }
        }
        jsonWriter.endObject();

        return jsonWriter;
    }

    static abstract class MiniWriter implements DidoJsonWriter {

        final String name;

        final FieldGetter getter;

        MiniWriter(String name, FieldGetter getter) {
            this.name = name;
            this.getter = getter;
        }

    }

    static class NestedWriter extends MiniWriter {

        private final DidoJsonWriter nestedSerializer;

        NestedWriter(String name, FieldGetter getter,
                     DidoJsonWriter nestedSerializer) {
            super(name, getter);
            this.nestedSerializer = nestedSerializer;
        }

        @Override
        public JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

            jsonWriter.name(name);
            if (getter.has(data)) {
                return nestedSerializer.write(
                        (DidoData) getter.get(data), jsonWriter);
            } else {
                return jsonWriter.nullValue();
            }
        }
    }

    static class RepeatingWriter extends MiniWriter {

        private final DidoJsonWriter nestedSerializer;

        RepeatingWriter(String name, FieldGetter getter,
                        DidoJsonWriter nestedSerializer) {
            super(name, getter);
            this.nestedSerializer = nestedSerializer;
        }

        @Override
        public JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

            jsonWriter.name(name);

            if (getter.has(data)) {
                jsonWriter.beginArray();
                RepeatingData src = (RepeatingData) getter.get(data);
                for (DidoData element : src) {
                    nestedSerializer.write(element, jsonWriter);
                }
                jsonWriter.endArray();
                return jsonWriter;
            } else {
                return jsonWriter.nullValue();
            }
        }
    }

    static class ObjectWriter extends MiniWriter {

        private final Gson gson;

        private final Type type;

        ObjectWriter(String name, FieldGetter getter,
                     Gson gson, Type type) {
            super(name, getter);
            this.gson = gson;
            this.type = type;
        }

        @Override
        public JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

            if (getter.has(data)) {
                jsonWriter.name(name);
                gson.toJson(getter.get(data), type, jsonWriter);
                return jsonWriter;
            } else {
                return jsonWriter.name(name)
                        .nullValue();
            }
        }
    }

    static class BooleanWriter extends MiniWriter {

        BooleanWriter(String name, FieldGetter getter) {
            super(name, getter);
        }

        @Override
        public JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

            jsonWriter.name(name);
            if (getter.has(data)) {
                return jsonWriter.value(getter.getBoolean(data));
            } else {
                return jsonWriter.nullValue();
            }
        }
    }

    static class IntWriter extends MiniWriter {

        IntWriter(String name, FieldGetter getter) {
            super(name, getter);
        }

        @Override
        public JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

            jsonWriter.name(name);
            if (getter.has(data)) {
                return jsonWriter.value(getter.getInt(data));
            } else {
                return jsonWriter.nullValue();
            }
        }
    }

    static class DoubleWriter extends MiniWriter {

        DoubleWriter(String name, FieldGetter getter) {
            super(name, getter);
        }

        @Override
        public JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

            jsonWriter.name(name);
            if (getter.has(data)) {
                return jsonWriter.value(getter.getDouble(data));
            } else {
                return jsonWriter.nullValue();
            }
        }
    }

    static class StringWriter extends MiniWriter {

        StringWriter(String name, FieldGetter getter) {
            super(name, getter);
        }

        @Override
        public
        JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

            jsonWriter.name(name);
            if (getter.has(data)) {
                return jsonWriter.value(getter.getString(data));
            } else {
                return jsonWriter.nullValue();
            }
        }
    }

    static class Unknown implements DidoJsonWriter {

        private final WriterConstructor writerConstructor;

        private DataSchema lastSchema;
        private DidoJsonWriter serializer;

        Unknown(Gson gson) {
            this.writerConstructor = new WriterConstructor(gson);
        }

        @Override
        public JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException {

            DataSchema schema = data.getSchema();
            if (!schema.equals(lastSchema)) {
                lastSchema = schema;
                serializer = writerConstructor.serializerFor(schema);
            }

            return serializer.write(data, jsonWriter);
        }
    }
}
