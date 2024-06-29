package dido.data;

import dido.data.generic.GenericSchemaField;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
 */
public interface DataSchema extends IndexedSchema {

    /**
     * Get the {@link GenericSchemaField} for the given index. A Schema Field will exist for
     * all valid indexes. Behaviour is undefined when the index is out of bounds.
     *
     * @param index The index, from 1.
     *
     * @return The Schema Field.
     */
    SchemaField getSchemaFieldAt(int index);

    /**
     * Get the field for the given index. If a field has not been allocated to the
     * given index null will be returned. Behaviour is undefined when
     * the index is out of bounds.
     *
     * @param index The index, from 1.
     *
     * @return F the field or null.
     */
    String getFieldNameAt(int index);

    /**
     * Get the nested schema a given index. If the
     * index does not exist behaviour is undefined.
     * If the data at the index is not nested this
     * method will return null. If the type of
     * this index is {@link IndexedData} or an
     * array of {@link IndexedData} then this method
     * will not return null.
     *
     * @param index The field.
     *
     * @return The nested schema.
     */
    DataSchema getSchemaAt(int index);

    /**
     * Get the {@link GenericSchemaField} for the given field. If the
     * field does not exist behaviour is undefined.
     *
     * @param fieldName The field.
     * @return The type.
     */
    SchemaField getSchemaFieldNamed(String fieldName);

    /**
     * Get the index for a given field. If the field does not
     * exist behaviour is undefined.
     *
     * @param fieldName The field.
     * @return The index of the field.
     */
    int getIndexNamed(String fieldName);

    /**
     * Get the type that a value is at a given field. If the
     * field does not exist behaviour is undefined.
     *
     * @param fieldName The field.
     * @return The type.
     */
    Class<?> getTypeNamed(String fieldName);

    /**
     * Get the nested schema for the given field. If the
     * field does not exist this method should return null, or if
     * there is not nested schema it will return null.
     *
     * @param fieldName The field.
     *
     * @return The nested schema or null.
     */
    DataSchema getSchemaNamed(String fieldName);

    /**
     * Get all the fields in this schema.
     *
     * @return A collection of fields. May be empty. Never null.
     */
    Collection<String> getFieldNames();

    /**
     * Get all the {@link GenericSchemaField}s in this schema.
     *
     * @return A collection of Schema Fields. May be empty. Never null.
     */
    Collection<SchemaField> getSchemaFields();

    /**
     * Compare two schemas for equality. Because it's forbidden to provide default Object methods in
     * interfaces each these statics are available as a convenience for implementations.
     *
     * @param schema1 The first schema. May be null.
     * @param schema2 The second schema. May be null.
     *
     * @return True if the fields types, the indexes, the types at each index and the
     * fields at each index are the same.
     */
    static boolean equals(DataSchema schema1, DataSchema schema2) {
        if (schema1 == schema2) {
            return true;
        }
        if (schema1 == null | schema2 == null) {
            return false;
        }
        if (schema1.lastIndex() != schema2.lastIndex()) {
            return false;
        }

        for (int index1 = schema1.firstIndex(), index2 = schema2.firstIndex();
             index1 > 0 || index2 > 0;
             index1 = schema1.nextIndex(index1), index2 = schema2.nextIndex(index2)) {

            if (index1 != index2) {
                return false;
            }
            if (!Objects.equals(schema1.getSchemaFieldAt(index1), schema2.getSchemaFieldAt(index2))) {
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
    static int hashCode(DataSchema schema) {
        int hash = 0;
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            hash = hash * 31 + schema.getSchemaFieldAt(index).hashCode();
        }
        return hash;
    }

    static String toString(DataSchema schema) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
            if (i > schema.firstIndex()) {
                sb.append(", ");
            }
            sb.append(schema.getSchemaFieldAt(i));
        }
        sb.append("}");
        return sb.toString();
    }

    static DataSchema emptySchema() {
        return new EmptySchema();
    }

    class EmptySchema implements DataSchema {

        @Override
        public int firstIndex() {
            return 0;
        }

        @Override
        public int nextIndex(int index) {
            return 0;
        }

        @Override
        public int lastIndex() {
            return 0;
        }

        @Override
        public SchemaField getSchemaFieldAt(int index) {
            return null;
        }

        @Override
        public String getFieldNameAt(int index) {
            return null;
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return null;
        }

        @Override
        public DataSchema getSchemaAt(int index) {
            return null;
        }

        @Override
        public SchemaField getSchemaFieldNamed(String fieldName) {
            return null;
        }

        @Override
        public int getIndexNamed(String fieldName) {
            return 0;
        }

        @Override
        public Class<?> getTypeNamed(String fieldName) {
            return null;
        }

        @Override
        public DataSchema getSchemaNamed(String fieldName) {
            return null;
        }

        @Override
        public Collection<String> getFieldNames() {
            return List.of();
        }

        @Override
        public Collection<SchemaField> getSchemaFields() {
            return List.of();
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DataSchema)) {
                return false;
            }
            DataSchema other = (DataSchema) obj;
            return other.firstIndex() == 0 && other.lastIndex() == 0;
        }

        @Override
        public String toString() {
            return "{}";
        }
    }
}
