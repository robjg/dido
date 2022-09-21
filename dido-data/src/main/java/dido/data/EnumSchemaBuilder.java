package dido.data;

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

    private final EnumMap<E, SchemaField<E>> fields;

    private EnumSchemaBuilder(Class<E> type) {
        this.type = type;
        this.fields = new EnumMap<>(type);
    }

    public static <E extends Enum<E>> EnumSchemaBuilder<E> forEnumType(Class<E> type) {

        return new EnumSchemaBuilder<>(type);
    }

    public EnumSchemaBuilder<E> addField(E field, Class<?> fieldType) {
        this.fields.put(field, SchemaFields.of(field.ordinal() + 1, field, fieldType));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addNestedField(E field, DataSchema<N> nestedSchema) {
        this.fields.put(field, SchemaFields.ofNested(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addRepeatingField(E field, DataSchema<N> nestedSchema) {
        this.fields.put(field, SchemaFields.ofRepeating(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addRepeatingField(E field, SchemaReference<N> nestedSchemaRef) {
        this.fields.put(field, SchemaFields.ofRepeating(field.ordinal() + 1, field, nestedSchemaRef));
        return this;
    }

    public EnumSchema<E> build() {

        EnumMap<E, SchemaField<E>> types = new EnumMap<>(this.fields);

        //noinspection unchecked
        return new Schema<>(this.type, types.values().toArray(new SchemaField[0]));
    }

    public static <E extends Enum<E>> EnumSchema<E> forTypeMapping(Class<E> enumClass,
                                                                   Function<E, Class<?>> typeMapping) {

        E[] enumConstants = enumClass.getEnumConstants();

        @SuppressWarnings("unchecked")
        SchemaField<E>[] fields = new SchemaField[enumConstants.length];

        for (int i = 0; i < fields.length; i++) {
            fields[i] = SchemaFields.of(i + 1, enumConstants[i], typeMapping.apply(enumConstants[i]));
        }

        return new Schema<>(enumClass, fields);
    }

    static class Schema<E extends Enum<E>> extends AbstractDataSchema<E> implements EnumSchema<E> {

        private final Class<E> enumClass;

        private final SchemaField<E>[] fields;

        Schema(Class<E> enumClass, SchemaField<E>[] fields) {
            this.enumClass = enumClass;
            this.fields = fields;
        }

        @Override
        public SchemaField<E> getSchemaFieldAt(int index) {
            return fields[index - 1];
        }

        @Override
        public Class<E> getFieldType() {
            return enumClass;
        }

        @Override
        public E getFieldAt(int index) {
            return fields[index - 1].getField();
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return fields[index - 1].getType();
        }

        @Override
        public <N> DataSchema<N> getSchemaAt(int index) {
            return fields[index - 1].getNestedSchema();
        }

        @Override
        public int getIndex(E field) {
            return field.ordinal() + 1;
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
        public Collection<E> getFields() {
            return Arrays.asList(enumClass.getEnumConstants());
        }

        @Override
        public int hashCode() {
            return DataSchema.hashCode(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DataSchema) {
                return DataSchema.equals(this, (DataSchema<?>) obj);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return DataSchema.toString(this);
        }
    }
}
