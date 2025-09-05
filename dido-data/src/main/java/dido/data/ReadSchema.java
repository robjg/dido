package dido.data;

import dido.data.schema.SchemaDelegate;

/**
 * A Data Schema that can provide the most efficient way of reading the
 * data it represents.
 */
public interface ReadSchema extends DataSchema, ReadStrategy {

    static ReadSchema emptySchema() {
        return new EmptySchema();
    }

    class EmptySchema extends DataSchema.EmptySchema implements ReadSchema {

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            throw new NoSuchFieldException(index, ReadSchema.EmptySchema.this);
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            throw new NoSuchFieldException(name, ReadSchema.EmptySchema.this);
        }
    }

    static ReadSchema from(DataSchema schema) {
        if (schema instanceof ReadSchema) {
            return (ReadSchema) schema;
        } else {
            return new DelegateReadSchema(schema);
        }
    }

    static ReadSchema from(DataSchema schema, ReadStrategy readStrategy) {
            return new DelegateReadSchema(schema, readStrategy);
    }

    class DelegateReadSchema extends SchemaDelegate implements ReadSchema {

        private final ReadStrategy readStrategy;

        DelegateReadSchema(DataSchema delegate) {
            this(delegate, ReadStrategy.fromSchema(delegate));
        }

        DelegateReadSchema(DataSchema delegate, ReadStrategy readStrategy) {
            super(delegate);
            this.readStrategy = readStrategy;
        }

        public FieldGetter getFieldGetterAt(int index) {
            return readStrategy.getFieldGetterAt(index);
        }

        public FieldGetter getFieldGetterNamed(String name) {
            return readStrategy.getFieldGetterNamed(name);
        }
    }
}