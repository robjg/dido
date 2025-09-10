package dido.data.enums;

import dido.data.SchemaField;
import dido.data.generic.GenericDataSchema;
import dido.data.schema.SchemaRefImpl;

import java.util.Objects;

/**
 * Builder for an {@link EnumSchema}.
 *
 * @param <E> The Enum type.
 */
public class EnumSchemaBuilder<E extends Enum<E>> {

    private final EnumSchemaFactory<E> schemaFactory;;

    public EnumSchemaBuilder(EnumSchemaFactory<E> schemaFactory) {
        this.schemaFactory = Objects.requireNonNull(schemaFactory);
    }

    public static <E extends Enum<E>> EnumSchemaBuilder<E> forEnumType(Class<E> enumClass) {

        return new EnumSchemaBuilder<>(EnumSchemaFactory.schemaFactoryFor(enumClass));
    }

    public EnumSchemaBuilder<E> addSchemaField(SchemaField schemaField) {
        schemaFactory.addSchemaField(schemaField);
        return this;
    }

    public EnumSchemaBuilder<E> addField(E field, Class<?> fieldType) {
        schemaFactory.addGenericSchemaField(schemaFactory.of()
                .of(field.ordinal() + 1, field, fieldType));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addNestedField(E field, GenericDataSchema<N> nestedSchema) {
        schemaFactory.addGenericSchemaField(schemaFactory.of()
                .ofNested(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addRepeatingField(E field, GenericDataSchema<N> nestedSchema) {
        schemaFactory.addGenericSchemaField(schemaFactory.of()
                .ofRepeating(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public EnumSchemaBuilder<E> addRepeatingField(E field, SchemaRefImpl nestedSchemaRef) {
        schemaFactory.addGenericSchemaField(schemaFactory.of()
                .ofRepeating(field.ordinal() + 1, field, nestedSchemaRef));
        return this;
    }

    public EnumSchema<E> build() {

        return schemaFactory.toSchema();

    }
}
