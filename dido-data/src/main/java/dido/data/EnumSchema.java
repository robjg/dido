package dido.data;

import dido.data.generic.GenericDataSchema;

import java.util.function.Function;

public interface EnumSchema<E extends Enum<E>> extends GenericDataSchema<E> {

    Class<E> getFieldType();

    static <E extends Enum<E>> EnumSchema<E> schemaFor(Class<E> enumClass, Function<E, Class<?>> typeMapping) {
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
