package dido.data;

import java.util.Collection;
import java.util.Objects;

/**
 * Define a Schema for Data. A schema is always defined by index but
 * may also be defined by field. The index is always greater than 0
 * and index may be not be sequential. The intention is to support reading
 * or writing column data (such as a csv file) where only some of the
 * columns are of interest.
 * <p/>
 * Instance of this type will be effectively immutable. Calling the same method repeatedly
 * with the same argument will always return the same value.
 *
 * @param <F> The type of the fields used in the schema.
 */
public interface DataSchema<F> {

    /**
     * The type of the fields in this schema. Generally a String or an enum.
     * Not to be confused with {@link #getType(F field)}.
     *
     * @return The type of the fields. Never null.
     */
    Class<F> getFieldType();

    /**
     * Get the field for the given index. If a field has not been allocated to the
     * given index null will be returned. Behaviour is undefined when
     * the index is out of bounds.
     *
     * @param index The index, from 1.
     *
     * @return F the field or null.
     */
    F getFieldAt(int index);


    /**
     * Get the type that a value is at a given index. If the
     * index does not exist behaviour is undefined.
     *
     * @param index The field.
     * @return The type.
     */
    Class<?> getTypeAt(int index);

    /**
     * Get the nested schema a given index. If the
     * index does not exist behaviour is undefined.
     * If the data at the index is not nested this
     * method will return null. If the type of
     * this index is {@link GenericData} or an
     * array of {@link GenericData} then this method
     * will not return null.
     *
     * @param index The field.
     * @return The nested schema.
     */
    <N> DataSchema<N> getSchemaAt(int index);

    /**
     * Get the index for a given field. If the field does not
     * exist behaviour is undefined.
     *
     * @param field The field.
     * @return The index of the field.
     */
    int getIndex(F field);

    /**
     * Get the first index in an {@link IndexedData} structure
     * at which there may be data. Calling {@link IndexedData#hasIndex(int)} for values
     * below this has undefined behaviour.
     *
     * @return The first index. Or a value of 0 if the schema is empty.
     */
    int firstIndex();

    /**
     * Get the next index {@link IndexedData} structure
     * at which there may be data. Calling {@link IndexedData#hasIndex(int)} for values
     * between the index argument and the returned value this has undefined behaviour.
     * Calling this method with a value that has not been returned by {@link #firstIndex()}
     * or a previous call to this method has undefined behaviour. Calling this method
     * with the result of {@link #lastIndex()} will always return 0.
     *
     * @param index A valid index.
     * @return The next index or 0.
     */
    int nextIndex(int index);

    /**
     * Get the first index in an {@link IndexedData} structure
     * at which there may be data. Calling {@link IndexedData#hasIndex(int)} for values
     * below this has undefined behaviour.
     *
     * @return The first index. Or a value of 0 if the schema is empty.
     */
    int lastIndex();

    /**
     * Get all the fields in this schema.
     *
     * @return A collection of fields. May be empty. Never null.
     */
    Collection<F> getFields();

    /**
     * Get the type that a value is at a given field. If the
     * field does not exist behaviour is undefined.
     *
     * @param field The field.
     * @return The type.
     */
    Class<?> getType(F field);

    /**
     * Get the nested schema for the given field. If the
     * field does not exist behaviour is undefined.
     * If the type of data for this field is {@link GenericData} or an
     * array of {@link GenericData} then this method
     * will not return null, otherwise it will return null.
     *
     * @param field The field.
     * @return The nested schema.
     */
    <N> DataSchema<N> getFieldSchema(F field);

    /**
     * Provide an empty schema.
     *
     * @param fieldType The type of the field.
     * @param <F> The type of the field.
     *
     * @return An empty schema.
     */
    static <F> DataSchema<F> emptySchema(Class<F> fieldType) {
        return SchemaBuilder.forFieldType(fieldType).build();
    }

    /**
     * Provide an empty schema for String fields.
     *
     * @return An empty schema.
     */
    static DataSchema<String> emptyStringFieldSchema() {
        return emptySchema(String.class);
    }

    /**
     * Compare two schemas for equality.
     *
     * @param schema1 The first schema. May be null.
     * @param schema2 The second schema. May be null.
     *
     * @return True if the fields types, the indexes, the types at each index and the
     * fields at each index are the same.
     */
    static boolean equals(DataSchema<?> schema1, DataSchema<?> schema2) {
        if (schema1 == schema2) {
            return true;
        }
        if (schema1 == null | schema2 == null) {
            return false;
        }
        if (schema1.getFieldType() != schema2.getFieldType()) {
            return false;
        }
        if (schema1.lastIndex() != schema2.lastIndex()) {
            return false;
        }

        for (int index1 = schema1.firstIndex(), index2 = schema2.firstIndex();
             index1 > 0 && index2 > 0;
             index1 = schema1.nextIndex(index1), index2 = schema2.nextIndex(index2)) {

            if (index1 != index2) {
                return false;
            }
            if (!Objects.equals(schema1.getTypeAt(index1), schema2.getTypeAt(index2))) {
                return false;
            }
            if (!Objects.equals(schema1.getFieldAt(index1), schema2.getFieldAt(index2))) {
                return false;
            }
            if (!Objects.equals(schema1.getSchemaAt(index1), schema2.getSchemaAt(index2))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Provide a hashcode for a schema.
     *
     * @param schema The schema.
     * @return A hash code value.
     */
    static int hashCode(DataSchema<?> schema) {
        int hash = 0;
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            hash = hash * 31 + Objects.hash(schema.getTypeAt(index), schema.getFieldAt(index), schema.getTypeAt(index));
        }
        return hash;
    }
}
