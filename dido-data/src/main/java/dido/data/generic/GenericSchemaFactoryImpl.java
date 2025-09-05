package dido.data.generic;

import dido.data.SchemaField;
import dido.data.schema.SchemaFactoryImpl;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract  public class GenericSchemaFactoryImpl<F, S extends GenericDataSchema<F>>
        extends SchemaFactoryImpl<S>
        implements GenericSchemaFactory<F> {

    private final Class<F> fieldType;

    private final GenericSchemaField.Of<F> of;

    protected GenericSchemaFactoryImpl(Class<F> fieldType,
                                       Function<? super String, ? extends F> mappingFunc) {
        this.fieldType = fieldType;
        this.of = GenericSchemaField.with(mappingFunc);
    }

    public Class<F> getFieldType() {
        return fieldType;
    }


    @Override
    public GenericSchemaField.Of<F> of() {
        return of;
    }

    @Override
    public GenericSchemaField<F> addSchemaField(SchemaField schemaField) {
        GenericSchemaField<F> useField;
        if (schemaField instanceof GenericSchemaField
                && ((GenericSchemaField<?>) schemaField).getField().getClass() == getFieldType()) {
            useField = (GenericSchemaField<F>) schemaField;
        }
        else {
            useField = of.from(schemaField);
        }
        return addGenericSchemaField(useField);
    }

    @Override
    public GenericSchemaField<F> addGenericSchemaField(GenericSchemaField<F> schemaField) {
        super.addSchemaField(schemaField);
        return schemaField;
    }

    protected S create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
        return createGeneric(fields.stream().map(f -> (GenericSchemaField<F>) f).collect(Collectors.toList()),
                firstIndex, lastIndex);
    }

    /**
     * implemented by subclasses to create the actual schema.
     *
     */
    protected abstract S createGeneric(Collection<GenericSchemaField<F>> fields, int firstIndex, int lastIndex);

    static class StringFields extends GenericSchemaFactoryImpl<String, GenericDataSchema<String>> {

        StringFields() {
            super(String.class, Function.identity());
        }

        @Override
        protected GenericDataSchema<String> createGeneric(Collection<GenericSchemaField<String>> genericSchemaFields, int firstIndex, int lastIndex) {
            return new GenericSchemaImpl<>(String.class, genericSchemaFields, firstIndex, lastIndex);
        }
    }

    static class FromFunction<F> extends GenericSchemaFactoryImpl<F, GenericDataSchema<F>> {

        FromFunction(Class<F> fieldType, Function<? super String, ? extends F> fieldMappingFunc) {
            super(fieldType, fieldMappingFunc);
        }

        @Override
        protected GenericDataSchema<F> createGeneric(Collection<GenericSchemaField<F>> genericSchemaFields, int firstIndex, int lastIndex) {
            return new GenericSchemaImpl<>(getFieldType(), genericSchemaFields, firstIndex, lastIndex);
        }
    }
}
