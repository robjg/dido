package dido.data.generic;

import dido.data.FieldGetter;
import dido.data.NoSuchFieldException;
import dido.data.ReadSchema;

public interface GenericReadSchema<F> extends GenericDataSchema<F>, ReadSchema, GenericReadStrategy<F> {

    /**
     * Provide an empty schema.
     *
     * @param fieldType The type of the field.
     *
     * @param <F> The type of the field.
     *
     * @return An empty schema.
     */
    static <F> GenericReadSchema<F> emptySchema(Class<F> fieldType) {
        return new EmptySchema<F>(fieldType);
    }


    class EmptySchema<F> extends GenericDataSchema.EmptySchema<F> implements GenericReadSchema<F> {

        public EmptySchema(Class<F> fieldType) {
            super(fieldType);
        }


        @Override
        public FieldGetter getFieldGetter(F field) {
            throw new NoSuchFieldException(field.toString(),
                    GenericReadSchema.EmptySchema.this);
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            throw new NoSuchFieldException(index,
                    GenericReadSchema.EmptySchema.this);
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            throw new NoSuchFieldException(name,
                    GenericReadSchema.EmptySchema.this);
        }
    }
}
