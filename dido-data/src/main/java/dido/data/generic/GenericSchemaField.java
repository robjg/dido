package dido.data.generic;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.data.SchemaReference;

import java.util.Objects;

/**
 * Definition of a field in a {@link GenericDataSchema}.
 *
 * @param <F> The Field type.
 */
public interface GenericSchemaField<F> extends SchemaField {

    default String getName() {
        F field = getField();
        return field == null ? null : field.toString();
    }

    F getField();

    DataSchema getNestedSchema();

    default GenericSchemaField<F> mapToIndex(int toIndex) {
        return mapTo(toIndex, (F) null);
    }

    default <T> GenericSchemaField<T> mapToField(T toField) {
        return mapTo(0, toField);
    }

    default <T> GenericSchemaField<T> mapTo(int toIndex, T toField) {

        if (toIndex == 0) {
            toIndex = getIndex();
        }

        if (toField == null) {
            //noinspection unchecked
            toField = (T) getField();
        }

        if (isNested()) {
            if (isRepeating()) {
                return ofRepeating(toIndex, toField, getNestedSchema());
            }
            else {
                return ofNested(toIndex, toField, getNestedSchema());
            }
        }
        else {
            return of(toIndex, toField, getType() );
        }
    }

    static <F> GenericSchemaField<F> of(int index, Class<?> type) {
        return GenericSchemaFields.of(index, null, type);
    }

    static <F> GenericSchemaField<F> of(int index, F field, Class<?> type) {
        return GenericSchemaFields.of(index, field, type);
    }

    static <F> GenericSchemaField<F> ofNested(int index, DataSchema nested) {
        return GenericSchemaFields.ofNested(index, null, nested);
    }

    static <F> GenericSchemaField<F> ofNested(int index, F field, DataSchema nested) {
        return GenericSchemaFields.ofNested(index, field, nested);
    }

    static <F> GenericSchemaField<F> ofNested(int index, SchemaReference nestedRef) {
        return GenericSchemaFields.ofNested(index, null, nestedRef);
    }

    static <F> GenericSchemaField<F> ofNested(int index, F field, SchemaReference nestedRef) {
        return GenericSchemaFields.ofNested(index, field, nestedRef);
    }

    static <F, N> GenericSchemaField<F> ofRepeating(int index, DataSchema nested) {
        return GenericSchemaFields.ofRepeating(index, null, nested);
    }

    static <F, N> GenericSchemaField<F> ofRepeating(int index, F field, DataSchema nested) {
        return GenericSchemaFields.ofRepeating(index, field, nested);
    }

    static <F> GenericSchemaField<F> ofRepeating(int index, SchemaReference nestedRef) {
        return GenericSchemaFields.ofRepeating(index, null, nestedRef);
    }

    static <F> GenericSchemaField<F> ofRepeating(int index, F field, SchemaReference nestedRef) {
        return GenericSchemaFields.ofRepeating(index, field, nestedRef);
    }

    static int hashCode(GenericSchemaField<?> schemaField) {
        return Objects.hash(
                schemaField.getIndex(),
                schemaField.getType(),
                schemaField.getField(),
                schemaField.isNested(),
                schemaField.isRepeating(),
                schemaField.getNestedSchema());
    }

    static boolean equals(GenericSchemaField<?> schemaField1, GenericSchemaField<?> schemaField2) {
        return schemaField1.getIndex() == schemaField2.getIndex()
                && schemaField1.getType() == schemaField2.getType()
                && schemaField1.isNested() == schemaField2.isNested()
                && schemaField1.isRepeating() == schemaField2.isRepeating()
                && Objects.equals(schemaField1.getField(), schemaField2.getField())
                && Objects.equals(schemaField1.getNestedSchema(), schemaField2.getNestedSchema());
    }
}
