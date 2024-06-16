package dido.data.generic;

import dido.data.DataSchema;

import java.util.Collection;
import java.util.Collections;

/**
 * Define a Schema for Data. A schema is always defined by index but
 * may also be defined by field. The index is always greater than 0
 * and index may be not be sequential. The intention is to support reading
 * or writing column data (such as a csv file) where only some of the
 * columns are of interest.
 * <p>
 * Instance of this type will be effectively immutable. Calling the same method repeatedly
 * with the same argument will always return the same value.
 * </p>
 *
 * @param <F> The type of the fields used in the schema.
 */
public interface GenericDataSchema<F> extends DataSchema {

    /**
     * Get the {@link GenericSchemaField} for the given index. A Schema Field will exist for
     * all valid indexes. Behaviour is undefined when the index is out of bounds.
     *
     * @param index The index, from 1.
     *
     * @return The Schema Field.
     */
    GenericSchemaField<F> getSchemaFieldAt(int index);

    /**
     * Get the field for the given index. If a field has not been allocated to the
     * given index null will be returned. Behaviour is undefined when
     * the index is out of bounds.
     *
     * @param index The index, from 1.
     *
     * @return F the field or null.
     *
     * @see #getFieldNameAt(int)
     */
    F getFieldAt(int index);

    /**
     * Get the {@link GenericSchemaField} for the given field. If the
     * field does not exist behaviour is undefined.
     *
     * @param fieldName The field.
     * @return The type.
     */
    GenericSchemaField<F> getSchemaFieldNamed(String fieldName);

    /**
     * Get the field of the given name. If a field
     * of the given name doesn't exist, behaviour is undefined.
     *
     * @param fieldName The name.
     * @return The field.
     */
    F getFieldNamed(String fieldName);

    /**
     * Get the {@link GenericSchemaField} for the given field. If the
     * field does not exist behaviour is undefined.
     *
     * @param field The field.
     * @return The type.
     */
    GenericSchemaField<F> getSchemaFieldOf(F field);

    /**
     * Get the index for a given field. If the field does not
     * exist behaviour is undefined.
     *
     * @param field The field.
     * @return The index of the field.
     */
    int getIndexOf(F field);

    /**
     * Get the field name of the given field. If the field does not exist
     * behaviour is undefined.
     *
     * @param field The field;
     *
     * @return THe field name.
     */
    String getFieldNameOf(F field);

    /**
     * Get the type that a value is at a given field. If the
     * field does not exist behaviour is undefined.
     *
     * @param field The field.
     * @return The type.
     */
    Class<?> getTypeOf(F field);

    /**
     * Get the nested schema for the given field. If the
     * field does not exist this method should return null, or if
     * there is not nested schema it will return null.
     *
     * @param field The field.
     *
     * @return The nested schema or null.
     */
    DataSchema getSchemaOf(F field);

    /**
     * Get all the fields in this schema.
     *
     * @return A collection of fields. May be empty. Never null.
     */
    Collection<F> getFields();

    /**
     * Provide an empty schema.
     *
     * @param <F> The type of the field.
     *
     * @return An empty schema.
     */
    static <F> GenericDataSchema<F> emptySchema() {
        return new EmptySchema<F>();
    }


    class EmptySchema<F> extends DataSchema.EmptySchema implements GenericDataSchema<F> {

        @Override
        public GenericSchemaField<F> getSchemaFieldAt(int index) {
            return null;
        }

        @Override
        public F getFieldAt(int index) {
            return null;
        }

        @Override
        public GenericSchemaField<F> getSchemaFieldNamed(String fieldName) {
            return null;
        }

        @Override
        public GenericSchemaField<F> getSchemaFieldOf(F field) {
            return null;
        }

        @Override
        public F getFieldNamed(String fieldName) {
            return null;
        }

        @Override
        public String getFieldNameOf(F field) {
            return "";
        }

        @Override
        public DataSchema getSchemaOf(F field) {
            return null;
        }

        @Override
        public Class<?> getTypeOf(F field) {
            return null;
        }

        @Override
        public int getIndexOf(F field) {
            return 0;
        }

        @Override
        public Collection<F> getFields() {
            return Collections.emptyList();
        }

    }

}
