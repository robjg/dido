package dido.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface EnumSchema<E extends Enum<E>> extends DataSchema<E> {

    Class<E> getFieldType();

    static <E extends Enum<E>> EnumSchema<E> schemaFor(Class<E> enumClass, Function<E, Class<?>> typeMapping) {
        return new EnumMapData.Schema<>(enumClass, typeMapping);
    }

    static <E extends Enum<E>> EnumSchema<E> enumSchemaFrom(DataSchema<String> delegate, Class<E> enumClass) {

        E[] enumConstants = enumClass.getEnumConstants();

        return new EnumSchema<E>() {
            @Override
            public Class<E> getFieldType() {
                return enumClass;
            }

            @Override
            public E getFieldAt(int index) {
                return enumConstants[index - 1];
            }

            @Override
            public Class<?> getTypeAt(int index) {
                return delegate.getTypeAt(index);
            }

            @Override
            public <N> DataSchema<N> getSchemaAt(int index) {
                return delegate.getSchemaAt(index);
            }

            @Override
            public int getIndex(E field) {
                return delegate.getIndex(field.toString());
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
                if (obj instanceof DataSchema) {
                    return DataSchema.equals(this, (DataSchema<?>) obj);
                }
                else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return DataSchema.hashCode(this);
            }

            @Override
            public String toString() {
                return DataSchema.toString(this);
            }
        };
    }

    static <E extends Enum<E>> DataSchema<String> stringSchemaFrom(EnumSchema<E> delegate) {

        return new DataSchema<String>() {

            @Override
            public String getFieldAt(int index) {
                return delegate.getFieldAt(index).toString();
            }

            @Override
            public Class<?> getTypeAt(int index) {
                return delegate.getTypeAt(index);
            }

            @Override
            public <N> DataSchema<N> getSchemaAt(int index) {
                return delegate.getSchemaAt(index);
            }

            @Override
            public int getIndex(String field) {
                return Enum.valueOf(delegate.getFieldType(), field).ordinal() + 1;
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
            public Collection<String> getFields() {
                return delegate.getFields().stream()
                        .map(Objects::toString)
                        .collect(Collectors.toList());
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof DataSchema) {
                    return DataSchema.equals(this, (DataSchema<?>) obj);
                }
                else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return DataSchema.hashCode(this);
            }

            @Override
            public String toString() {
                return DataSchema.toString(this);
            }
        };
    }
}
