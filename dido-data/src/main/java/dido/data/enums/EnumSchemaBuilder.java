package dido.data.enums;

import dido.data.SchemaField;
import dido.data.generic.GenericDataSchema;
import dido.data.schema.SchemaDefs;

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

    // Setup

    public EnumSchemaBuilder<E> withSchemaName(String schemaName) {
        schemaFactory.setSchemaName(schemaName);
        return this;
    }

    public EnumSchemaBuilder<E> withSchemaDefs(SchemaDefs defs) {
        schemaFactory.setSchemaDefs(defs);
        return this;
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

    public <N> EnumSchemaBuilder<E> addRef(E field, String refSchemaName) {
        schemaFactory.addSchemaReference(schemaFactory.of()
                .ofRef(field.ordinal() + 1, field, refSchemaName));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addRepeatingField(E field, GenericDataSchema<N> nestedSchema) {
        schemaFactory.addGenericSchemaField(schemaFactory.of()
                .ofRepeating(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public EnumSchemaBuilder<E> addRepeatingRef(E field, String refSchemaName) {
        schemaFactory.addSchemaReference(schemaFactory.of()
                .ofRepeatingRef(field.ordinal() + 1, field, refSchemaName));
        return this;
    }

    public EnumSchema<E> build() {

        return schemaFactory.toSchema();

    }
}
