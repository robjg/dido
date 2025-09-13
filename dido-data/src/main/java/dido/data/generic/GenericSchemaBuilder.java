package dido.data.generic;


import dido.data.DataSchema;
import dido.data.schema.SchemaDefs;

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

    // Setup

    public GenericSchemaBuilder<F, S> withSchemaName(String schemaName) {
        schemaFactory.setSchemaName(schemaName);
        return this;
    }

    public GenericSchemaBuilder<F, S> withSchemaDefs(SchemaDefs defs) {
        schemaFactory.setSchemaDefs(defs);
        return this;
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

    public GenericSchemaBuilder<F, S> addNestedRefField(F field,
                                                  String refSchemaName) {
        return addNestedRefFieldAt(0, field, refSchemaName);
    }

    public GenericSchemaBuilder<F, S> addNestedRefFieldAt(int index,
                                                          F field,
                                                          String refSchemaName) {
        schemaFactory.addSchemaReference(schemaFactory.of().ofRef(index, field, refSchemaName));
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

    public GenericSchemaBuilder<F, S> addRepeatingRef(String refSchemaName) {
        return addRepeatingRefAt(0, refSchemaName);
    }

    public GenericSchemaBuilder<F, S> addRepeatingRefAt(int index,
                                                        String refSchemaName) {
        return addRepeatingRefFieldAt(index, null, refSchemaName);
    }

    public GenericSchemaBuilder<F, S> addRepeatingRefField(F field,
                                                           String refSchemaName) {
        return addRepeatingRefFieldAt(0, field, refSchemaName);
    }

    public GenericSchemaBuilder<F, S> addRepeatingRefFieldAt(int index,
                                                             F field,
                                                             String refSchemaName) {

        schemaFactory.addSchemaReference(schemaFactory.of().ofRepeatingRef(index, field, refSchemaName));
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
