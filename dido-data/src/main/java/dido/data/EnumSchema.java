package dido.data;

import dido.data.generic.GenericDataSchema;
import dido.data.generic.GenericSchemaBuilder;
import dido.data.generic.GenericSchemaField;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public interface EnumSchema<E extends Enum<E>> extends GenericDataSchema<E> {

    Class<E> getFieldType();

    static <E extends Enum<E>> EnumSchema<E> schemaFor(Class<E> enumClass, Function<E, Class<?>> typeMapping) {
        return EnumSchemaBuilder.forTypeMapping(enumClass, typeMapping);
    }

    static <E extends Enum<E>> EnumSchema<E> enumSchemaFrom(GenericDataSchema<String> original,
                                                            Class<E> enumClass) {

        E[] enumConstants = enumClass.getEnumConstants();

        GenericDataSchema<E> delegate = original.getSchemaFields()
                .stream()
                .reduce(GenericSchemaBuilder.forFieldType(enumClass),
                        (b, sf) -> b.addGenericSchemaField(
                                GenericSchemaField.of(sf.getIndex(), enumConstants[sf.getIndex() - 1], sf.getType())),
                        (b1, b2) -> b1)
                .build();

        return new EnumSchema<>() {

            @Override
            public Class<E> getFieldType() {
                return enumClass;
            }

            @Override
            public E getFieldNamed(String fieldName) {
                return delegate.getFieldNamed(fieldName);
            }

            @Override
            public String getFieldNameAt(int index) {
                return delegate.getFieldNameAt(index);
            }

            @Override
            public int getIndexNamed(String fieldName) {
                return delegate.getIndexNamed(fieldName);
            }

            @Override
            public Collection<String> getFieldNames() {
                return delegate.getFieldNames();
            }

            @Override
            public GenericSchemaField<E> getSchemaFieldNamed(String fieldName) {
                return delegate.getSchemaFieldNamed(fieldName);
            }

            @Override
            public Class<?> getTypeNamed(String fieldName) {
                return delegate.getTypeNamed(fieldName);
            }

            @Override
            public DataSchema getSchemaNamed(String fieldName) {
                return delegate.getSchemaNamed(fieldName);
            }

            @Override
            public int getIndexOf(E field) {
                return delegate.getIndexOf(field);
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
            public GenericSchemaField<E> getSchemaFieldAt(int index) {
                return delegate.getSchemaFieldAt(index);
            }

            @Override
            public E getFieldAt(int index) {
                return delegate.getFieldAt(index);
            }

            @Override
            public Class<?> getTypeAt(int index) {
                return delegate.getTypeAt(index);
            }

            @Override
            public DataSchema getSchemaAt(int index) {
                return delegate.getSchemaAt(index);
            }

            @Override
            public Collection<SchemaField> getSchemaFields() {
                return delegate.getSchemaFields();
            }

            @Override
            public GenericSchemaField<E> getSchemaFieldOf(E field) {
                return delegate.getSchemaFieldOf(field);
            }

            @Override
            public String getFieldNameOf(E field) {
                return delegate.getFieldNameOf(field);
            }

            @Override
            public Class<?> getTypeOf(E field) {
                return delegate.getTypeOf(field);
            }

            @Override
            public DataSchema getSchemaOf(E field) {
                return delegate.getSchemaOf(field);
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

}
