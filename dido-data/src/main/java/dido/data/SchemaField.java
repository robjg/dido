package dido.data;

import java.util.Objects;

/**
 * Definition of a field in an {@link DataSchema}.
 * Schema fields should always be equal if their index, type and name match regardless of any other attributes
 * they might have.
 */
public interface SchemaField {

    int getIndex();

    Class<?> getType();

    boolean isNested();

    boolean isRepeating();

    String getName();

    DataSchema getNestedSchema();

    default SchemaField mapToIndex(int toIndex) {
        return mapTo(toIndex, null);
    }

    default SchemaField mapToField(String toField) {
        return mapTo(0, toField);
    }

    default SchemaField mapTo(int toIndex, String toField) {

        if (toIndex == 0) {
            toIndex = getIndex();
        }

        if (toField == null) {
            toField = getName();
        }

        if (isNested()) {
            if (isRepeating()) {
                return ofRepeating(toIndex, toField, getNestedSchema());
            } else {
                return ofNested(toIndex, toField, getNestedSchema());
            }
        } else {
            return of(toIndex, toField, getType());
        }
    }

    static SchemaField of(int index, Class<?> type) {
        return SchemaFields.of(index, type);
    }

    static SchemaField of(int index, String field, Class<?> type) {
        return SchemaFields.of(index, field, type);
    }

    static SchemaField ofNested(int index, DataSchema nested) {
        return SchemaFields.ofNested(index, nested);
    }

    static SchemaField ofNested(int index, String field, DataSchema nested) {
        return SchemaFields.ofNested(index, field, nested);
    }

    static SchemaField ofNested(int index, SchemaReference<?> nestedRef) {
        return SchemaFields.ofNested(index, nestedRef);
    }

    static SchemaField ofNested(int index, String field, SchemaReference<?> nestedRef) {
        return SchemaFields.ofNested(index, field, nestedRef);
    }

    static SchemaField ofRepeating(int index, DataSchema nested) {
        return SchemaFields.ofRepeating(index, nested);
    }

    static SchemaField ofRepeating(int index, String field, DataSchema nested) {
        return SchemaFields.ofRepeating(index, field, nested);
    }

    static SchemaField ofRepeating(int index, SchemaReference<?> nestedRef) {
        return SchemaFields.ofRepeating(index, nestedRef);
    }

    static SchemaField ofRepeating(int index, String field, SchemaReference<?> nestedRef) {
        return SchemaFields.ofRepeating(index, field, nestedRef);
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
