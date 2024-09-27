package dido.data.generic;

import dido.data.FieldGetter;
import dido.data.NoSuchFieldException;
import dido.data.ReadableSchema;

public interface GenericReadableSchema<F> extends GenericDataSchema<F>, ReadableSchema {

    default FieldGetter getDataGetter(F field) {
        if (!hasField(field)) {
            throw new NoSuchFieldException(field.toString(), this);
        }
        else {
            return getFieldGetterAt(getIndexOf(field));
        }
    }

    /**
     * Provide an empty schema.
     *
     * @param fieldType The type of the field.
     *
     * @param <F> The type of the field.
     *
     * @return An empty schema.
     */
    static <F> GenericReadableSchema<F> emptySchema(Class<F> fieldType) {
        return new EmptySchema<F>(fieldType);
    }


    class EmptySchema<F> extends GenericDataSchema.EmptySchema<F> implements GenericReadableSchema<F> {

        public EmptySchema(Class<F> fieldType) {
            super(fieldType);
        }


        @Override
        public FieldGetter getDataGetter(F field) {
            throw new NoSuchFieldException(field.toString(),
                    GenericReadableSchema.EmptySchema.this);
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            throw new NoSuchFieldException(index,
                    GenericReadableSchema.EmptySchema.this);
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            throw new NoSuchFieldException(name,
                    GenericReadableSchema.EmptySchema.this);
        }
    }
}
