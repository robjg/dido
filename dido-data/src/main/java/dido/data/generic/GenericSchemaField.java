package dido.data.generic;

import dido.data.DataSchema;
import dido.data.SchemaField;

import java.util.Objects;
import java.util.function.Function;

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
        return mapTo(toIndex, getField());
    }

    default GenericSchemaField<F> mapToField(F toField) {
        return mapTo(getIndex(), toField);
    }

    GenericSchemaField<F> mapTo(int toIndex, F toField);

    static <F> Of<F> with(Function<? super String, ? extends F> fieldMappingFunc) {
        return new GenericSchemaFields<>(fieldMappingFunc);
    }

    interface Of<F> {

        GenericSchemaField<F> of(int index, String name, Class<?> type);

        GenericSchemaField<F> of(int index, F field, Class<?> type);

        GenericSchemaField<F> ofNested(int index, String name, DataSchema nested);

        GenericSchemaField<F> ofNested(int index, F field, DataSchema nested);

        SchemaField.RefFactory ofRef(int index, String name, String refSchemaName);

        SchemaField.RefFactory ofRef(int index, F field, String refSchemaName);

        GenericSchemaField<F> ofRepeating(int index, String field, DataSchema nested);

        GenericSchemaField<F> ofRepeating(int index, F field, DataSchema nested);

        SchemaField.RefFactory ofRepeatingRef(int index, String field, String refSchemaName);

        SchemaField.RefFactory ofRepeatingRef(int index, F field, String refSchemaName);

        GenericSchemaField<F> from(SchemaField schemaField);
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
