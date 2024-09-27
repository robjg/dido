package dido.data;

import dido.data.generic.AbstractGenericDataSchema;
import dido.data.generic.GenericDataSchema;
import dido.data.generic.GenericSchemaField;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builder for an {@link EnumSchema}.
 *
 * @param <E> The Enum type.
 */
public class EnumSchemaBuilder<E extends Enum<E>> {

    private final Class<E> type;

    private final GenericSchemaField.Of<E> genericField;

    private final EnumMap<E, GenericSchemaField<E>> fields;

    private EnumSchemaBuilder(Class<E> type) {
        this.type = Objects.requireNonNull(type);
        this.genericField = GenericSchemaField.with(name -> Enum.valueOf(type, name));
        this.fields = new EnumMap<>(type);
    }

    public static <E extends Enum<E>> EnumSchemaBuilder<E> forEnumType(Class<E> type) {

        return new EnumSchemaBuilder<>(type);
    }

    public EnumSchemaBuilder<E> addSchemaField(SchemaField schemaField) {
        E field = Enum.valueOf(type, schemaField.getName());
        this.fields.put(field, genericField.of(field.ordinal() + 1, field, schemaField.getType()));
        return this;
    }

    public EnumSchemaBuilder<E> addField(E field, Class<?> fieldType) {
        this.fields.put(field, genericField.of(field.ordinal() + 1, field, fieldType));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addNestedField(E field, GenericDataSchema<N> nestedSchema) {
        this.fields.put(field, genericField.ofNested(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public <N> EnumSchemaBuilder<E> addRepeatingField(E field, GenericDataSchema<N> nestedSchema) {
        this.fields.put(field, genericField.ofRepeating(field.ordinal() + 1, field, nestedSchema));
        return this;
    }

    public EnumSchemaBuilder<E> addRepeatingField(E field, SchemaReference nestedSchemaRef) {
        this.fields.put(field, genericField.ofRepeating(field.ordinal() + 1, field, nestedSchemaRef));
        return this;
    }

    public EnumSchema<E> build() {

        EnumMap<E, GenericSchemaField<E>> types = new EnumMap<>(this.fields);

        //noinspection unchecked
        return new Schema<>(this.type, types.values().toArray(new GenericSchemaField[0]));
    }

    public static <E extends Enum<E>> EnumSchema<E>
    forTypeMapping(Class<E> enumClass,
                   Function<? super E, ? extends Class<?>> typeMapping) {

        E[] enumConstants = enumClass.getEnumConstants();

        GenericSchemaField.Of<E> genericField = GenericSchemaField.with(name -> Enum.valueOf(enumClass, name));

        @SuppressWarnings("unchecked")
        GenericSchemaField<E>[] fields = new GenericSchemaField[enumConstants.length];

        for (int i = 0; i < fields.length; i++) {
            fields[i] = genericField.of(i + 1, enumConstants[i], typeMapping.apply(enumConstants[i]));
        }

        return new Schema<>(enumClass, fields);
    }

    static class Schema<E extends Enum<E>> extends AbstractGenericDataSchema<E> implements EnumSchema<E> {

        private final Class<E> enumClass;

        private final GenericSchemaField<E>[] fields;

        private final Map<String, GenericSchemaField<E>> byName;

        Schema(Class<E> enumClass, GenericSchemaField<E>[] fields) {
            this.enumClass = enumClass;
            this.fields = fields;
            this.byName = Arrays.stream(fields)
                    .collect(Collectors.toMap(GenericSchemaField::getName, Function.identity()));
        }

        @Override
        public Class<E> getFieldType() {
            return enumClass;
        }

        @Override
        public boolean hasIndex(int index) {
            return index > 0 && index < fields.length;
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
        public boolean hasNamed(String name) {
            return byName.containsKey(name);
        }

        @Override
        public E getFieldNamed(String fieldName) {
            GenericSchemaField<E> field = byName.get(fieldName);
            return field == null ? null : field.getField();
        }

        @Override
        public int getIndexNamed(String name) {
            GenericSchemaField<E> field = byName.get(name);
            return field == null ? 0 : field.getIndex();
        }

        @Override
        public int getIndexOf(E field) {
            return field.ordinal() + 1;
        }

        @Override
        public boolean hasField(E field) {
            return true;
        }

        @Override
        public Collection<GenericSchemaField<E>> getGenericSchemaFields() {
            return Arrays.asList(fields);
        }

        @Override
        public Collection<E> getFields() {
            return Arrays.asList(enumClass.getEnumConstants());
        }

        @Override
        public FieldGetter getDataGetter(E field) {
            return new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    return ((EnumData<E>) data).get(field);
                }
            };
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            E field = Schema.this.getFieldAt(index);
            if (field == null) {
                throw new NoSuchFieldException(index, Schema.this);
            }
            return getDataGetter(field);
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            E field = Schema.this.getFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, Schema.this);
            }
            return getDataGetter(field);
        }
    }
}
