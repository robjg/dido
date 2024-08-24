package dido.data;

import java.util.Objects;

/**
 * Definition of a field in an {@link DataSchema}.
 * Schema fields should always be equal if their index, type and name match regardless of any other attributes
 * they might have.
 */
public interface SchemaField {

    Class<DidoData> NESTED_TYPE = DidoData.class;

    Class<RepeatingData> NESTED_REPEATING_TYPE = RepeatingData.class;

    /**
     * The index of the field. A Schema Field that belongs to a schema will always have an
     * index > 0. A Schema Field can have an index < 1 it hasn't been assigned to a Schema yet.
     *
     * @return The index, if one has been assigned.
     */
    int getIndex();

    /**
     * The name of the field. A Schema Field that belongs to a schema will always have a
     * none empty name. A Schema Field can have a null name if it hasn't been assigned to a Schema yet.
     * A Schema Field should never have an empty name.
     *
     * @return The name, if one has been assigned, null otherwise.
     */
    String getName();

    /**
     * The type of the field. Never null.
     *
     * @return The type.
     */
    Class<?> getType();

    /**
     * Does this field contain a nested Schema Definition. The {@link #getType()} of a nested definition
     * will always be {@link #NESTED_TYPE} or a super class of this.
     *
     * @return true if it is nested, false otherwise.
     */
    boolean isNested();

    /**
     * Does this field contain a repeating nested Schema Definition. The {@link #getType()} of a repeating definition
     * will always be {@link #NESTED_REPEATING_TYPE} or a super class of this. A Schema Field will never be Repeating
     * but Not Nested.
     *
     * @return true if it is nested and repeating, false otherwise.
     */
    boolean isRepeating();

    /**
     * Get the Nested Schema.
     *
     * @return The Nested Schema. Will be null if the Schema Field is not Nested.
     */
    DataSchema getNestedSchema();

    default SchemaField mapToIndex(int toIndex) {
        return mapTo(toIndex, getName());
    }

    default SchemaField mapToFieldName(String toName) {
        return mapTo(getIndex(), toName);
    }

    SchemaField mapTo(int toIndex, String toName);

    static SchemaField of(int index, String name, Class<?> type) {
        return SchemaFields.of(index, name, type);
    }

    static SchemaField ofNested(int index, String name, DataSchema nested) {
        return SchemaFields.ofNested(index, name, nested);
    }

    static SchemaField ofNested(int index, String name, SchemaReference nestedRef) {
        return SchemaFields.ofNested(index, name, nestedRef);
    }

    static SchemaField ofRepeating(int index, String name, DataSchema nested) {
        return SchemaFields.ofRepeating(index, name, nested);
    }

    static SchemaField ofRepeating(int index, String name, SchemaReference nestedRef) {
        return SchemaFields.ofRepeating(index, name, nestedRef);
    }

    static int hash(SchemaField field) {
        return field.getIndex();
    }

    static boolean equals(SchemaField field1, SchemaField field2) {
        if (field1 == field2) {
            return true;
        }
        if (field1 == null | field2 == null) {
            return false;
        }

        return field1.getIndex() == field2.getIndex() &&
                Objects.equals(field1.getName(), field2.getName()) &&
                Objects.equals(field1.getType(), field2.getType()) &&
                field1.isNested() == field2.isNested() &&
                field1.isRepeating() == field2.isRepeating() &&
                DataSchema.equals(field1.getNestedSchema(), field2.getNestedSchema());
    }

}
