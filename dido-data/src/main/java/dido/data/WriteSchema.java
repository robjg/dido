package dido.data;

import dido.data.useful.SchemaDelegate;

/**
 * A {@link DataSchema} that is able to support a transformation of the {@link DidoData} this is a schema for.
 */
public interface WriteSchema extends DataSchema, WriteStrategy {

    static WriteSchema from(DataSchema schema) {
        if (schema instanceof WriteSchema) {
            return (WriteSchema) schema;
        } else {
            return new DelegateWriteSchema(schema);
        }
    }

    static WriteSchema from(DataSchema schema, WriteStrategy writeStrategy) {
        return new DelegateWriteSchema(schema, writeStrategy);
    }

    class DelegateWriteSchema extends SchemaDelegate implements WriteSchema {

        private final WriteStrategy writeStrategy;

        DelegateWriteSchema(DataSchema delegate) {
            this(delegate, WriteStrategy.fromSchema(delegate));
        }

        DelegateWriteSchema(DataSchema delegate, WriteStrategy writeStrategy) {
            super(delegate);
            this.writeStrategy = writeStrategy;
        }

        @Override
        public FieldSetter getFieldSetterAt(int index) {
            return writeStrategy.getFieldSetterAt(index);
        }

        @Override
        public FieldSetter getFieldSetterNamed(String name) {
            return writeStrategy.getFieldSetterNamed(name);
        }
    }
}
