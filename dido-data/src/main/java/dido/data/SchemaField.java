package dido.data;

import java.util.Objects;

/**
 * Definition of a field in a {@link DataSchema}.
 *
 * @param <F> The Field type.
 */
public interface SchemaField<F> {

    Class<?> NESTED_TYPE = IndexedData.class;

    Class<?> NESTED_REPEATING_TYPE = IndexedData[].class;

    enum Is {

        SIMPLE() {
            @Override
            boolean isNested() {
                return false;
            }

            @Override
            boolean isRepeating() {
                return false;
            }
        },

        NESTED() {
            @Override
            boolean isNested() {
                return true;
            }

            @Override
            boolean isRepeating() {
                return false;
            }
        },

        REPEATING() {
            @Override
            boolean isNested() {
                return true;
            }

            @Override
            boolean isRepeating() {
                return true;
            }
        };

        abstract boolean isNested();

        abstract boolean isRepeating();
    }

    int getIndex();

    Class<?> getType();

    Is getIs();

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

        if (getIs() == SchemaField.Is.SIMPLE) {
            return of(toIndex, toField, getType() );
        }
        else if (getIs() == SchemaField.Is.NESTED) {
            return ofNested(toIndex, toField, getNestedSchema());
        }
        else if (getIs() == SchemaField.Is.REPEATING) {
            return ofRepeating(toIndex, toField, getNestedSchema());
        }
        else {
            throw new UnsupportedOperationException();
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
                schemaField.getIs(),
                schemaField.getNestedSchema());
    }

    static boolean equals(SchemaField<?> schemaField1, SchemaField<?> schemaField2) {
        return schemaField1.getIndex() == schemaField2.getIndex()
                && schemaField1.getType() == schemaField2.getType()
                && schemaField1.getIs() == schemaField2.getIs()
                && Objects.equals(schemaField1.getField(), schemaField2.getField())
                && Objects.equals(schemaField1.getNestedSchema(), schemaField2.getNestedSchema());
    }
}
