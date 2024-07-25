package dido.data;

import dido.data.generic.GenericDataSchema;

import java.util.function.Function;

/**
 * Definition of a {@link DataSchema} for {@link EnumData}.
 *
 * @param <E> The Enum type.
 */
public interface EnumSchema<E extends Enum<E>> extends GenericDataSchema<E> {

    Class<E> getFieldType();

    /**
     * Provide an Enum Schema from an Enum class with a Function that provides the type for each enum value.
     *
     * @param enumClass The enum class.
     * @param typeMapping The mapping to a type.
     *
     * @return An enum schema.
     *
     * @param <E> the type pf enum.
     */
    static <E extends Enum<E>> EnumSchema<E> schemaFor(Class<E> enumClass,
                                                       Function<? super E, ? extends Class<?>> typeMapping) {
        return EnumSchemaBuilder.forTypeMapping(enumClass, typeMapping);
    }

    static <E extends Enum<E>> EnumSchema<E> enumSchemaFrom(DataSchema original,
                                                            Class<E> enumClass) {

        E[] enumConstants = enumClass.getEnumConstants();

        return original.getSchemaFields()
                .stream()
                .reduce(EnumSchemaBuilder.forEnumType(enumClass),
                        EnumSchemaBuilder::addSchemaField,
                        (b1, b2) -> b1)
                .build();
    }
}
