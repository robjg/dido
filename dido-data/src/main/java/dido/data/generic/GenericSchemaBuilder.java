package dido.data.generic;


import dido.data.DataSchema;
import dido.data.SchemaReference;

import java.util.function.Function;

/**
 * Builder for an {@link GenericDataSchema}.
 *
 * @param <F> The field type.
 */
public class GenericSchemaBuilder<F, S extends GenericDataSchema<F>> {

    private final GenericSchemaFactory<F> schemaFactory;

    private GenericSchemaBuilder(GenericSchemaFactory<F> schemaFactory) {
        this.schemaFactory = schemaFactory;
    }

    public static <F, S extends GenericDataSchema<F>> GenericSchemaBuilder<F, S> builderFor(
            GenericSchemaFactory<F> schemaFactory
    ) {
        return new GenericSchemaBuilder<>(schemaFactory);
    }

    public static GenericSchemaBuilder<String, GenericDataSchema<String>> forStringFields() {
        return new GenericSchemaBuilder<>(new GenericSchemaFactoryImpl.StringFields());
    }

    public static <F, S extends GenericDataSchema<F>> GenericSchemaBuilder<F, S> builderFor(
            GenericSchemaFactory<F> schemaFactory, Class<S> schemaClass) {
        return new GenericSchemaBuilder<>(schemaFactory);
    }


    public static <F, S extends GenericDataSchema<F>> GenericSchemaBuilder<F, S> forFieldType(Class<F> fieldType,
                                                           Function<? super String, ? extends F> fieldMappingFunc) {


        return new GenericSchemaBuilder<>(new GenericSchemaFactoryImpl.FromFunction<>(fieldType, fieldMappingFunc));
    }

    // Add Simple Fields

    public GenericSchemaBuilder<F, S> addField(F field, Class<?> fieldType) {
        return addFieldAt(0, field, fieldType);
    }

    public GenericSchemaBuilder<F, S> addFieldAt(int index, F field, Class<?> fieldType) {

        schemaFactory.addGenericSchemaField(schemaFactory.of().of(index, field, fieldType));
        return this;
    }

    // Add Nested Field

    public GenericSchemaBuilder<F, S> addNestedField(F field,
                                                  DataSchema nestedSchema) {
        return addNestedFieldAt(0, field, nestedSchema);
    }

    public GenericSchemaBuilder<F, S> addNestedFieldAt(int index,
                                                    F field,
                                                    DataSchema nestedSchema) {

        schemaFactory.addGenericSchemaField(schemaFactory.of().ofNested(index, field, nestedSchema));
        return this;
    }

    // Add Nested Reference

    public GenericSchemaBuilder<F, S> addNestedField(F field,
                                                  SchemaReference nestedSchemaRef) {
        return addNestedFieldAt(0, field, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F, S> addNestedFieldAt(int index,
                                                        F field,
                                                        SchemaReference nestedSchemaRef) {
        schemaFactory.addGenericSchemaField(schemaFactory.of().ofNested(index, field, nestedSchemaRef));
        return this;
    }

    // Add Repeating Nested Schema

    public GenericSchemaBuilder<F, S> addRepeatingField(F field,
                                                     DataSchema nestedSchema) {
        return addRepeatingFieldAt(0, field, nestedSchema);
    }

    public GenericSchemaBuilder<F, S> addRepeatingFieldAt(int index,
                                                       F field,
                                                       DataSchema nestedSchema) {
        schemaFactory.addGenericSchemaField(schemaFactory.of().ofRepeating(index, field, nestedSchema));
        return this;
    }

    // Add Repeating Nested Schema Ref

    public GenericSchemaBuilder<F, S> addRepeating(SchemaReference nestedSchemaRef) {
        return addRepeatingAt(0, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F, S> addRepeatingAt(int index,
                                                  SchemaReference nestedSchemaRef) {
        return addRepeatingFieldAt(index, null, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F, S> addRepeatingField(F field,
                                                     SchemaReference nestedSchemaRef) {
        return addRepeatingFieldAt(0, field, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F, S> addRepeatingFieldAt(int index,
                                                       F field,
                                                       SchemaReference nestedSchemaRef) {

        schemaFactory.addGenericSchemaField(schemaFactory.of().ofRepeating(index, field, nestedSchemaRef));
        return this;
    }


    public GenericSchemaBuilder<F, S> merge(GenericDataSchema<F> prioritySchema) {

        schemaFactory.merge(prioritySchema);
        return this;
    }


    public GenericSchemaBuilder<F, S> addGenericSchemaField(GenericSchemaField<F> schemaField) {

        schemaFactory.addGenericSchemaField(schemaField);

        return this;
    }

    public GenericDataSchema<F> build() {
        return schemaFactory.toSchema();
    }

}
