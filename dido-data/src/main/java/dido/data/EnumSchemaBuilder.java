package dido.data;

import dido.data.generic.AbstractGenericDataSchema;
import dido.data.generic.GenericDataSchema;
import dido.data.generic.GenericSchemaField;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.function.Function;

/**
 * Builder for an {@link EnumSchema}.
 *
 * @param <E> The Enum type.
 */
public class EnumSchemaBuilder<E extends Enum<E>> {

    private final Class<E> type;

    private final EnumMap<E, GenericSchemaField<E>> fields;

    private EnumSchemaBuilder(Class<E> type) {
        this.type = type;
        this.fields = new EnumMap<>(type);
    }

    public static <E extends Enum<E>> EnumSchemaBuilder<E> forEnumType(Class<E> type) {

        return new EnumSchemaBuilder<>(type);
    }

    public EnumSchemaBuilder<E> addField(E field, Class<?> fieldType) {
        this.fields.put(field, GenericSchemaField.of(field.ordinal() + 1, field, fieldType));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addNestedField(E field, GenericDataSchema<N> nestedSchema) {
        this.fields.put(field, GenericSchemaField.ofNested(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addRepeatingField(E field, GenericDataSchema<N> nestedSchema) {
        this.fields.put(field, GenericSchemaField.ofRepeating(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public EnumSchemaBuilder<E> addRepeatingField(E field, SchemaReference nestedSchemaRef) {
        this.fields.put(field, GenericSchemaField.ofRepeating(field.ordinal() + 1, field, nestedSchemaRef));
        return this;
    }

    public EnumSchema<E> build() {

        EnumMap<E, GenericSchemaField<E>> types = new EnumMap<>(this.fields);

        //noinspection unchecked
        return new Schema<>(this.type, types.values().toArray(new GenericSchemaField[0]));
    }

    public static <E extends Enum<E>> EnumSchema<E> forTypeMapping(Class<E> enumClass,
                                                                   Function<E, Class<?>> typeMapping) {

        E[] enumConstants = enumClass.getEnumConstants();

        @SuppressWarnings("unchecked")
        GenericSchemaField<E>[] fields = new GenericSchemaField[enumConstants.length];

        for (int i = 0; i < fields.length; i++) {
            fields[i] = GenericSchemaField.of(i + 1, enumConstants[i], typeMapping.apply(enumConstants[i]));
        }

        return new Schema<>(enumClass, fields);
    }

    static class Schema<E extends Enum<E>> extends AbstractGenericDataSchema<E> implements EnumSchema<E> {

        private final Class<E> enumClass;

        private final GenericSchemaField<E>[] fields;

        Schema(Class<E> enumClass, GenericSchemaField<E>[] fields) {
            this.enumClass = enumClass;
            this.fields = fields;
        }

        @Override
        public Class<E> getFieldType() {
            return enumClass;
        }

        @Override
        public int firstIndex() {
            return 1;
        }

        @Override
        public int nextIndex(int index) {
            return index < fields.length ? index + 1 : 0;
        }

        @Override
        public int lastIndex() {
            return fields.length;
        }

        @Override
        public GenericSchemaField<E> getSchemaFieldAt(int index) {
            return fields[index - 1];
        }

        @Override
        public E getField(String fieldName) {
            return null;
        }

        @Override
        public int getIndexNamed(String fieldName) {
            return 0;
        }

        @Override
        public int getIndex(E field) {
            return field.ordinal() + 1;
        }

        @Override
        public Collection<E> getFields() {
            return Arrays.asList(enumClass.getEnumConstants());
        }

    }
}
