package dido.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.*;

public class DescriptorSchema extends AbstractDataSchema implements ReadableSchema {

    private final static Map<Descriptors.FieldDescriptor.JavaType, Class<?>> types =
            Map.of(Descriptors.FieldDescriptor.JavaType.INT, int.class,
                    Descriptors.FieldDescriptor.JavaType.LONG, long.class,
                    Descriptors.FieldDescriptor.JavaType.FLOAT, float.class,
                    Descriptors.FieldDescriptor.JavaType.DOUBLE, double.class,
                    Descriptors.FieldDescriptor.JavaType.BOOLEAN, boolean.class,
                    Descriptors.FieldDescriptor.JavaType.STRING, String.class,
                    Descriptors.FieldDescriptor.JavaType.ENUM, Enum.class,
                    Descriptors.FieldDescriptor.JavaType.MESSAGE, Message.class);

    private final Map<String, Pair> byName;
    private final NavigableMap<Integer, Pair> byIndex;

    private DescriptorSchema(List<Pair> pairs) {

        byName = new HashMap<>(pairs.size());
        byIndex = new TreeMap<>();

        for (Pair pair : pairs) {
            byName.put(pair.schemaField.getName(), pair);
            byIndex.put(pair.schemaField.getIndex(), pair);
        }
    }

    public static DescriptorSchema from(Descriptors.Descriptor descriptor) {

        List<Descriptors.FieldDescriptor> fieldDescriptors = descriptor.getFields();

        List<Pair> pairs = new ArrayList<>(fieldDescriptors.size());

        for (Descriptors.FieldDescriptor fieldDescriptor : fieldDescriptors) {

            SchemaField schemaField = SchemaField.of(
                    fieldDescriptor.getIndex() + 1,
                    fieldDescriptor.getName(),
                    types.get(fieldDescriptor.getJavaType()));

            pairs.add(new Pair(fieldDescriptor, schemaField));
        }

        return new DescriptorSchema(pairs);
    }

    public Descriptors.FieldDescriptor getFieldDescriptorAt(int index) {
        Pair pair = byIndex.get(index);
        return pair == null ? null : pair.fieldDescriptor;
    }

    public Descriptors.FieldDescriptor getFieldDescriptorNamed(String name) {
        Pair pair = byName.get(name);
        return pair == null ? null : pair.fieldDescriptor;
    }

    @Override
    public boolean hasIndex(int index) {
        return byIndex.get(index) != null;
    }

    @Override
    public SchemaField getSchemaFieldAt(int index) {
        Pair pair = byIndex.get(index);
        return pair == null ? null : pair.schemaField;
    }

    @Override
    public SchemaField getSchemaFieldNamed(String name) {
        Pair pair = byName.get(name);
        return pair == null ? null : pair.schemaField;
    }

    @Override
    public int firstIndex() {
        return Optional.ofNullable(byIndex.firstEntry())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    @Override
    public int nextIndex(int index) {
        Integer next = byIndex.higherKey(index);
        return next == null ? 0 : next;
    }

    @Override
    public int lastIndex() {
        return Optional.ofNullable(byIndex.lastEntry())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    @Override
    public boolean hasNamed(String name) {
        return byName.get(name) != null;
    }

    @Override
    public FieldGetter getFieldGetterAt(int index) {
        Descriptors.FieldDescriptor fieldDescriptor = getFieldDescriptorAt(index);
        if (fieldDescriptor == null) {
            throw new NoSuchFieldException(index, this);
        }
        return new AbstractFieldGetter() {
            @Override
            public Object get(DidoData data) {
                return ((DynamicMessageData) data).message.getField(fieldDescriptor);
            }
        };
    }

    @Override
    public FieldGetter getFieldGetterNamed(String name) {
        Descriptors.FieldDescriptor fieldDescriptor = getFieldDescriptorNamed(name);
        if (fieldDescriptor == null) {
            throw new NoSuchFieldException(name, this);
        }
        return new AbstractFieldGetter() {
            @Override
            public Object get(DidoData data) {
                return ((DynamicMessageData) data).message.getField(fieldDescriptor);
            }
        };
    }

    private static class Pair {

        private final Descriptors.FieldDescriptor fieldDescriptor;

        private final SchemaField schemaField;

        private Pair(Descriptors.FieldDescriptor fieldDescriptor, SchemaField schemaField) {
            this.fieldDescriptor = fieldDescriptor;
            this.schemaField = schemaField;
        }
    }
}
