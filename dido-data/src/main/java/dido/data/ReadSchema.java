package dido.data;

import dido.data.useful.AbstractFieldGetter;
import dido.data.useful.SchemaDelegate;

/**
 * A Data Schema that can provide the most efficient way of reading the
 * data it represents.
 */
public interface ReadSchema extends DataSchema {



    default FieldGetter getFieldGetterAt(int index) {
        if (!hasIndex(index)) {
            throw new NoSuchFieldException(index, this);
        } else {
            return new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    return data.getAt(index);
                }
            };
        }
    }

    default FieldGetter getFieldGetterNamed(String name) {
        if (!hasNamed(name)) {
            throw new NoSuchFieldException(name, this);
        } else {
            return getFieldGetterAt(getIndexNamed(name));
        }
    }

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

    static ReadSchema readableSchemaFrom(DataSchema schema) {
        if (schema instanceof ReadSchema) {
            return (ReadSchema) schema;
        } else {
            return new DelegateReadSchema(schema);
        }
    }

    class DelegateReadSchema extends SchemaDelegate implements ReadSchema {

        public DelegateReadSchema(DataSchema delegate) {
            super(delegate);
        }
    }
}