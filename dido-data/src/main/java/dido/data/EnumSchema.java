package dido.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public interface EnumSchema<E extends Enum<E>> extends DataSchema<E> {

    Class<E> getFieldType();

    static <E extends Enum<E>> EnumSchema<E> schemaFor(Class<E> enumClass, Function<E, Class<?>> typeMapping) {
        return EnumSchemaBuilder.forTypeMapping(enumClass, typeMapping);
    }

    static <E extends Enum<E>> EnumSchema<E> enumSchemaFrom(DataSchema<String> original,
                                                            Class<E> enumClass) {

        E[] enumConstants = enumClass.getEnumConstants();

        DataSchema<E> delegate = original.getSchemaFields()
                .stream()
                .reduce(SchemaBuilder.forFieldType(enumClass),
                        (b, sf) -> b.addSchemaField(
                                sf.mapToField(enumConstants[sf.getIndex() - 1])),
                        (b1, b2) -> b1)
                .build();

        return new EnumSchema<>() {
            @Override
            public Class<E> getFieldType() {
                return enumClass;
            }

            @Override
            public SchemaField<E> getSchemaFieldAt(int index) {
                return delegate.getSchemaFieldAt(index);
            }

            @Override
            public int getIndex(E field) {
                return delegate.getIndex(field);
            }

            @Override
            public int firstIndex() {
                return delegate.firstIndex();
            }

            @Override
            public int nextIndex(int index) {
                return delegate.nextIndex(index);
            }

            @Override
            public int lastIndex() {
                return delegate.lastIndex();
            }

            @Override
            public Collection<E> getFields() {
                return Arrays.asList(enumConstants);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof EnumSchema) {
                    EnumSchema<?> other = (EnumSchema<?>)  obj;
                    return enumClass == other.getFieldType()
                        && delegate.equals(other);
                } else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return Objects.hash(enumClass, delegate);
            }

            @Override
            public String toString() {
                return enumClass.getSimpleName() + ": " + delegate.toString();
            }
        };
    }

    static <E extends Enum<E>> DataSchema<String> stringSchemaFrom(EnumSchema<E> original) {

        return original.getSchemaFields()
                .stream()
                .reduce(SchemaBuilder.forStringFields(),
                        (b, sf) -> b.addSchemaField(
                                sf.mapToField(sf.getField().toString())),
                        (b1, b2) -> b1)
                .build();

    }
}
