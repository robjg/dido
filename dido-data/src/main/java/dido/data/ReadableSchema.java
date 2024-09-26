package dido.data;

/**
 * A Data Schema that can provide the most efficient way of reading the
 * data it represents.
 */
public interface ReadableSchema extends DataSchema {

    default Getter getDataGetterAt(int index) {
        if (!hasIndex(index)) {
            throw new NoSuchFieldException(index, this);
        } else {
            return new AbstractGetter() {
                @Override
                public Object get(DidoData data) {
                    return data.getAt(index);
                }
            };
        }
    }

    default Getter getDataGetterNamed(String name) {
        if (!hasNamed(name)) {
            throw new NoSuchFieldException(name, this);
        } else {
            return getDataGetterAt(getIndexNamed(name));
        }
    }

    static ReadableSchema emptySchema() {
        return new EmptySchema();
    }

    class EmptySchema extends DataSchema.EmptySchema implements ReadableSchema {

        @Override
        public Getter getDataGetterAt(int index) {
            throw new NoSuchFieldException(index, ReadableSchema.EmptySchema.this);
        }

        @Override
        public Getter getDataGetterNamed(String name) {
            throw new NoSuchFieldException(name, ReadableSchema.EmptySchema.this);
        }
    }

    static ReadableSchema readableSchemaFrom(DataSchema schema) {
        if (schema instanceof ReadableSchema) {
            return (ReadableSchema) schema;
        } else {
            return new DelegateReadableSchema(schema);
        }
    }

    class DelegateReadableSchema extends AbstractDataSchema implements ReadableSchema {

        private final DataSchema delegate;

        public DelegateReadableSchema(DataSchema delegate) {
            this.delegate = delegate;
        }

        @Override
        public SchemaField getSchemaFieldAt(int index) {
            return delegate.getSchemaFieldAt(index);
        }

        @Override
        public boolean hasIndex(int index) {
            return delegate.hasIndex(index);
        }

        @Override
        public int firstIndex() {
            return delegate.firstIndex();
        }

        @Override
        public int nextIndex(int index) {
            return delegate.nextIndex(index);
        }

        @Override
        public int lastIndex() {
            return delegate.lastIndex();
        }

        @Override
        public boolean hasNamed(String name) {
            return delegate.hasNamed(name);
        }

        @Override
        public SchemaField getSchemaFieldNamed(String name) {
            return delegate.getSchemaFieldNamed(name);
        }
    }

}