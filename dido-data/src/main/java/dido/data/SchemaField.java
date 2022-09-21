package dido.data;

import java.util.Objects;

/**
 * Definition of a field in a {@link DataSchema}.
 *
 * @param <F> The Field type.
 */
public interface SchemaField<F> {

    Class<?> NESTED_TYPE = GenericData.class;

    Class<?> NESTED_REPEATING_TYPE = RepeatingData.class;

    int getIndex();

    Class<?> getType();

    boolean isNested();

    boolean isRepeating();

    F getField();

    <N> DataSchema<N> getNestedSchema();

    default SchemaField<F> mapToIndex(int toIndex) {
        return mapTo(toIndex, null);
    }

    default <T> SchemaField<T> mapToField(T toField) {
        return mapTo(0, toField);
    }

    default <T> SchemaField<T> mapTo(int toIndex, T toField) {

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

    static <F> SchemaField<F> of(int index, Class<?> type) {
        return SchemaFields.of(index, type);
    }

    static <F> SchemaField<F> of(int index, F field, Class<?> type) {
        return SchemaFields.of(index, field, type);
    }

    static <F, N> SchemaField<F> ofNested(int index, DataSchema<N> nested) {
        return SchemaFields.ofNested(index, nested);
    }

    static <F, N> SchemaField<F> ofNested(int index, F field, DataSchema<N> nested) {
        return SchemaFields.ofNested(index, field, nested);
    }

    static <F, N> SchemaField<F> ofNested(int index, SchemaReference<N> nestedRef) {
        return SchemaFields.ofNested(index, nestedRef);
    }

    static <F, N> SchemaField<F> ofNested(int index, F field, SchemaReference<N> nestedRef) {
        return SchemaFields.ofNested(index, field, nestedRef);
    }

    static <F, N> SchemaField<F> ofRepeating(int index, DataSchema<N> nested) {
        return SchemaFields.ofRepeating(index, nested);
    }

    static <F, N> SchemaField<F> ofRepeating(int index, F field, DataSchema<N> nested) {
        return SchemaFields.ofRepeating(index, field, nested);
    }

    static <F, N> SchemaField<F> ofRepeating(int index, SchemaReference<N> nestedRef) {
        return SchemaFields.ofRepeating(index, nestedRef);
    }

    static <F, N> SchemaField<F> ofRepeating(int index, F field, SchemaReference<N> nestedRef) {
        return SchemaFields.ofRepeating(index, field, nestedRef);
    }

    static int hashCode(SchemaField<?> schemaField) {
        return Objects.hash(
                schemaField.getIndex(),
                schemaField.getType(),
                schemaField.getField(),
                schemaField.isNested(),
                schemaField.isRepeating(),
                schemaField.getNestedSchema());
    }

    static boolean equals(SchemaField<?> schemaField1, SchemaField<?> schemaField2) {
        return schemaField1.getIndex() == schemaField2.getIndex()
                && schemaField1.getType() == schemaField2.getType()
                && schemaField1.isNested() == schemaField2.isNested()
                && schemaField1.isRepeating() == schemaField2.isRepeating()
                && Objects.equals(schemaField1.getField(), schemaField2.getField())
                && Objects.equals(schemaField1.getNestedSchema(), schemaField2.getNestedSchema());
    }
}
