package dido.data;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Utility methods to provide {@link AnonymousData}.
 */
public class AnonymousDatas {

    public static AnonymousData wrap(IndexedData original) {

        return wrapOfSchema(original.getSchema()).apply(original);
    }

    public static AnonymousData copy(IndexedData original) {

        return copyOfSchema(original.getSchema()).apply(original);
    }

    public static AnonymousData partialWrap(IndexedData original, int... indices) {

        return partialWrapOfSchema(original.getSchema(), indices).apply(original);
    }

    public static AnonymousData partialCopy(IndexedData original, int... indices) {

        return partialCopyOfSchema(original.getSchema(), indices).apply(original);
    }

    public static Function<IndexedData, AnonymousData>
    wrapOfSchema(IndexedSchema original) {

        int[] indices = new int[original.lastIndex()];
        int i = 0;
        for (int j = original.firstIndex(); j > 0; j = original.nextIndex(j)) {
            indices[i++] = j;
        }
        return partialWrapOfSchema(original, Arrays.copyOf(indices, i));
    }

    public static Function<IndexedData, AnonymousData>
    partialWrapOfSchema(IndexedSchema original, int... indices) {

        return new WrapFunc(new WrapSchema(original, indices));
    }

    public static Function<IndexedData, AnonymousData>
    copyOfSchema(IndexedSchema original) {

        Class<?>[] types = new Class[original.lastIndex()];
        int[] indices = new int[original.lastIndex()];
        int count = 0;
        for (int i = original.firstIndex(); i > 0; i = original.nextIndex(i)) {
            types[count] = original.getTypeAt(i);
            indices[count] = i;
            ++count;
        }

        types = Arrays.copyOf(types, count);
        indices = Arrays.copyOf(indices, count);

        return new CopyFunc(new CopySchema(types), indices);
    }

    public static Function<IndexedData, AnonymousData>
    partialCopyOfSchema(IndexedSchema original, int... indices) {

        Class<?>[] types = new Class[indices.length];
        for (int i = 0; i < types.length; ++i) {
            types[i] = original.getTypeAt(indices[i]);
        }

        return new CopyFunc(new CopySchema(types), indices);
    }

    static class WrapFunc implements Function<IndexedData, AnonymousData> {

        private final WrapSchema schema;

        WrapFunc(WrapSchema schema) {
            this.schema = schema;
        }

        @Override
        public AnonymousData apply(IndexedData indexedData) {

            return new Wrap(schema, indexedData);
        }
    }

    static class Wrap extends AbstractAnonymousData {

        private final WrapSchema schema;

        private final IndexedData original;

        Wrap(WrapSchema schema, IndexedData original) {
            this.schema = schema;
            this.original = original;
        }

        @Override
        public AnonymousSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            return original.getAt(schema.indices[index - 1]);
        }
    }

    static class WrapSchema extends AbstractAnonymousSchema implements AnonymousSchema {

        private final IndexedSchema original;

        private final int last;

        private final int[] indices;


        WrapSchema(IndexedSchema original, int[] indices) {
            this.original = original;
            this.indices = indices;
            this.last = indices.length;
        }

        @Override
        public boolean hasIndex(int index) {
            return index > 0 && index < last;
        }

        @Override
        public int firstIndex() {
            return indices.length == 0 ? 0 : 1;
        }

        @Override
        public int nextIndex(int index) {
            return index >= last ? 0 : index + 1;
        }

        @Override
        public int lastIndex() {
            return last;
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return original.getTypeAt(indices[index - 1]);
        }
    }

    static class CopyFunc implements Function<IndexedData, AnonymousData> {

        private final AnonymousSchema schema;

        private final int[] indices;

        CopyFunc(AnonymousSchema schema,
                 int[] indices) {
            this.indices = indices;
            this.schema = schema;
        }

        @Override
        public AnonymousData apply(IndexedData indexedData) {
            Object[] values = new Object[indices.length];

            for (int i = 0; i < values.length; ++i) {
                values[i] = indexedData.getAt(indices[i]);
            }

            return new Copy(schema, values);
        }
    }

    static class Copy extends AbstractAnonymousData {

        private final AnonymousSchema schema;

        private final Object[] values;

        Copy(AnonymousSchema schema,
             Object[] values) {
            this.schema = schema;
            this.values = values;
        }



        @Override
        public AnonymousSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            return values[index - 1];
        }

    }

    static class CopySchema extends AbstractAnonymousSchema implements AnonymousSchema {

        private final int last;

        private final Class<?>[] types;

        private CopySchema(Class<?>[] types) {
            this.last = types.length;
            this.types = types;
        }

        @Override
        public boolean hasIndex(int index) {
            return index > 0 && index < last;
        }

        @Override
        public int firstIndex() {
            return types.length == 0 ? 0 : 1;
        }

        @Override
        public int nextIndex(int index) {
            return index >= last ? 0 : index + 1;
        }

        @Override
        public int lastIndex() {
            return last;
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return types[index - 1];
        }
    }

    static class SingleSchema extends AbstractAnonymousSchema implements AnonymousSchema {

        private final Class<?> type;

        SingleSchema(Class<?> type) {
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
        public Class<?> getTypeAt(int index) {
            if (index == 1) {
                return type;
            }
            else {
                return null;
            }
        }
    }
}
