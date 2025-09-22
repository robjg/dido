package dido.data.immutable;

import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.useful.AbstractCompactData;
import dido.data.useful.AbstractCompactSchema;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * Utility methods to provide {@link CompactData}.
 */
public class CompactDatas {


    public static CompactData.Extractor
    extractorForIndices(DataSchema original, int... indices) {

        ReadSchema readSchema = ReadSchema.from(original);

        if (indices.length == 1) {
            Type type = readSchema.getTypeAt(indices[0]);
            FieldGetter getter = readSchema.getFieldGetterAt(indices[0]);
            if (type == int.class) {
                return new SingleIntExtractor(getter);
            } else {
                return new SingleValueExtractor(type, getter);
            }
        } else {
            Type[] types = new Class[indices.length];
            FieldGetter[] getters = new FieldGetter[indices.length];
            for (int i = 0; i < types.length; ++i) {
                types[i] = readSchema.getTypeAt(indices[i]);
                getters[i] = readSchema.getFieldGetterAt(indices[i]);
            }

            return new MuchDataExtractor(new MuchDataSchema(types), getters);
        }
    }

    public static CompactData.Extractor
    extractorForNames(DataSchema original, String... names) {

        int[] indices = Arrays.stream(names)
                .mapToInt(name -> {
                    int index = original.getIndexNamed(name);
                    if (index < 1) {
                        throw new dido.data.NoSuchFieldException(name, original);
                    }
                    return index;
                }).toArray();

        return extractorForIndices(original, indices);
    }


    static class SingleValueExtractor implements CompactData.Extractor {

        private final SingleSchema schema;

        private final FieldGetter getter;

        SingleValueExtractor(Type type, FieldGetter getter) {
            this.schema = new SingleSchema(type);
            this.getter = getter;
        }

        @Override
        public CompactSchema getCompactSchema() {
            return schema;
        }

        @Override
        public CompactData apply(DidoData data) {
            return new SingleValue(schema, getter.get(data));
        }
    }

    static class SingleIntExtractor implements CompactData.Extractor {

        private final SingleSchema schema = new SingleSchema(int.class);

        private final FieldGetter getter;

        SingleIntExtractor(FieldGetter getter) {
            this.getter = getter;
        }

        @Override
        public CompactSchema getCompactSchema() {
            return schema;
        }

        @Override
        public CompactData apply(DidoData data) {
            return new SingleInt(schema, getter.getInt(data));
        }
    }

    static class MuchDataExtractor implements CompactData.Extractor {

        private final MuchDataSchema schema;

        private final FieldGetter[] getters;

        MuchDataExtractor(MuchDataSchema schema, FieldGetter[] getters) {
            this.schema = schema;
            this.getters = getters;
        }

        @Override
        public CompactSchema getCompactSchema() {
            return schema;
        }

        @Override
        public CompactData apply(DidoData data) {

            Object[] values = new Object[getters.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = getters[i].get(data);
            }
            return new MuchData(schema, values);
        }
    }


    static class MuchDataSchema extends AbstractCompactSchema implements CompactSchema {

        private final Type[] types;

        private MuchDataSchema(Type[] types) {
            this.types = types;
        }

        @Override
        public boolean hasIndex(int index) {
            return index > 0 && index < types.length;
        }

        @Override
        public int firstIndex() {
            return types.length == 0 ? 0 : 1;
        }

        @Override
        public int nextIndex(int index) {
            return index >= types.length ? 0 : index + 1;
        }

        @Override
        public int lastIndex() {
            return types.length;
        }

        @Override
        public int getSize() {
            return types.length;
        }

        @Override
        public Type getTypeAt(int index) {
            return types[index - 1];
        }
    }

    static class MuchData extends AbstractCompactData {

        private final MuchDataSchema schema;

        private final Object[] values;

        private volatile int hashCode = -1;

        MuchData(MuchDataSchema schema,
                 Object[] values) {
            this.schema = schema;
            this.values = values;
        }

        @Override
        public CompactSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            return values[index - 1];
        }

        @Override
        public int hashCode() {
            if (hashCode == -1) {
                hashCode = Arrays.hashCode(values);
            }
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MuchData) {
                return Arrays.equals(((MuchData) obj).values, values);
            }
            else {
                return false;
            }
        }
    }

    static class SingleSchema extends AbstractCompactSchema implements CompactSchema {

        private final Type type;

        SingleSchema(Type type) {
            this.type = type;
        }

        @Override
        public boolean hasIndex(int index) {
            return index == 1;
        }

        @Override
        public int firstIndex() {
            return 1;
        }

        @Override
        public int nextIndex(int index) {
            return 0;
        }

        @Override
        public int lastIndex() {
            return 1;
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public Type getTypeAt(int index) {
            if (index == 1) {
                return type;
            } else {
                return null;
            }
        }
    }

    static class SingleValue extends AbstractCompactData {

        private final CompactSchema schema;

        private final Object value;

        SingleValue(CompactSchema schema, Object value) {
            this.schema = Objects.requireNonNull(schema);
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public CompactSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            if (index == 1) {
                return value;
            } else {
                throw new dido.data.NoSuchFieldException(index, schema);
            }
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SingleValue) {
                return ((SingleValue) obj).value.equals(value);
            } else {
                return false;
            }
        }
    }

    static class SingleInt extends AbstractCompactData {

        private final CompactSchema schema;

        private final int value;

        SingleInt(CompactSchema schema, int value) {
            this.schema = Objects.requireNonNull(schema);
            this.value = value;
        }

        @Override
        public CompactSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            if (index == 1) {
                return value;
            } else {
                throw new NoSuchFieldException(index, schema);
            }
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SingleInt) {
                return ((SingleInt) obj).value == value;
            } else {
                return false;
            }
        }
    }

}
