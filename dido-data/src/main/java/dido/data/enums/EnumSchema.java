package dido.data.enums;

import dido.data.DataSchema;
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
     * @param enumClass   The enum class.
     * @param typeMapping The mapping to a type.
     * @param <E>         the type pf enum.
     * @return An enum schema.
     */
    static <E extends Enum<E>> EnumSchema<E> schemaFor(Class<E> enumClass,
                                                       Function<? super E, ? extends Class<?>> typeMapping) {

        E[] enumConstants = enumClass.getEnumConstants();

        EnumSchemaFactory<E> factory = EnumSchemaFactory.schemaFactoryFor(enumClass);

        for (int i = 0; i < enumConstants.length; i++) {
            factory.addSchemaField(factory.of()
                    .of(i + 1, enumConstants[i], typeMapping.apply(enumConstants[i])));
        }

        return factory.toSchema();
    }

    static <E extends Enum<E>> EnumSchema<E> enumSchemaFrom(DataSchema original,
                                                            Class<E> enumClass) {

        return original.getSchemaFields()
                .stream()
                .reduce(EnumSchemaFactory.schemaFactoryFor(enumClass),
                        (factory, field) -> {
                            factory.addSchemaField(field);
                            return factory;
                        },
                        (b1, b2) -> b1)
                .toSchema();
    }

}
