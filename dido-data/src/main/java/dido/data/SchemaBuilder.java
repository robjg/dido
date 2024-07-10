package dido.data;

/**
 * Builder for an {@link DataSchema}.
 */
public class SchemaBuilder<S extends DataSchema> {

    private final SchemaFactory<S> schemaFactory;

    private SchemaBuilder(SchemaFactory<S> schemaFactory) {
        this.schemaFactory = schemaFactory;
    }

    public static <S extends DataSchema> SchemaBuilder<S> builderFor(SchemaFactory<S> schemaFactory) {
        return new SchemaBuilder<>(schemaFactory);
    }

    public static SchemaBuilder<DataSchema> newInstance() {
        return new SchemaBuilder<>(SchemaFactoryImpl.newInstance());
    }


    // Add Simple Fields

    public SchemaBuilder<S> add(Class<?> fieldType) {
        return addAt(0, fieldType);
    }

    public SchemaBuilder<S> addAt(int index, Class<?> fieldType) {
        return addNamedAt(index, null, fieldType);
    }

    public SchemaBuilder<S> addNamed(String name, Class<?> fieldType) {
        return addNamedAt(0, name, fieldType);
    }

    /**
     * Add a field, replacing as necessary.
     * If the index is 0, the next available is used. If the index exists the field is
     * replaced.
     * If the name is null, the name is derived from the index. If the name exists
     * a new name is used.
     *
     * @param index     The index.
     * @param name      The name.
     * @param fieldType The field type. Must not be null.
     * @return This builder
     */
    public SchemaBuilder<S> addNamedAt(int index, String name, Class<?> fieldType) {

        schemaFactory.addNamedAt(index, name, fieldType);
        return this;
    }

    // Add Nested Field

    public SchemaBuilder<S> addNested(DataSchema nestedSchema) {
        return addNestedAt(0, nestedSchema);
    }

    public SchemaBuilder<S> addNestedAt(int index,
                                     DataSchema nestedSchema) {
        return addNestedNamedAt(index, null, nestedSchema);
    }

    public SchemaBuilder<S> addNestedNamed(String name,
                                        DataSchema nestedSchema) {
        return addNestedNamedAt(0, name, nestedSchema);
    }

    public SchemaBuilder<S> addNestedNamedAt(int index,
                                          String name,
                                          DataSchema nestedSchema) {

        schemaFactory.addNestedNamedAt(index, name, nestedSchema);
        return this;
    }

    // Add Nested Reference

    public SchemaBuilder<S> addNested(SchemaReference nestedSchemaRef) {
        return addNestedAt(0, nestedSchemaRef);
    }

    public SchemaBuilder<S> addNestedAt(int index,
                                     SchemaReference nestedSchemaRef) {
        return addNestedNamedAt(index, null, nestedSchemaRef);
    }

    public SchemaBuilder<S> addNestedNamed(String field,
                                        SchemaReference nestedSchemaRef) {
        return addNestedNamedAt(0, field, nestedSchemaRef);
    }

    public SchemaBuilder<S> addNestedNamedAt(int index,
                                          String name,
                                          SchemaReference nestedSchemaRef) {

        schemaFactory.addNestedNamedAt(index, name, nestedSchemaRef);
        return this;
    }

    // Add Repeating Nested Schema

    public SchemaBuilder<S> addRepeating(DataSchema nestedSchema) {
        return addRepeatingAt(0, nestedSchema);
    }

    public SchemaBuilder<S> addRepeatingAt(int index,
                                        DataSchema nestedSchema) {
        return addRepeatingNamedAt(index, null, nestedSchema);
    }

    public SchemaBuilder<S> addRepeatingNamed(String name,
                                           DataSchema nestedSchema) {
        return addRepeatingNamedAt(0, name, nestedSchema);
    }

    public SchemaBuilder<S> addRepeatingNamedAt(int index,
                                             String name,
                                             DataSchema nestedSchema) {
        schemaFactory.addRepeatingNamedAt(index, name, nestedSchema);
        return this;
    }

    // Add Repeating Nested Schema Ref

    public SchemaBuilder<S> addRepeating(SchemaReference nestedSchemaRef) {
        return addRepeatingAt(0, nestedSchemaRef);
    }

    public SchemaBuilder<S> addRepeatingAt(int index,
                                        SchemaReference nestedSchemaRef) {
        return addRepeatingNamedAt(index, null, nestedSchemaRef);
    }

    public SchemaBuilder<S> addRepeatingNamed(String name,
                                           SchemaReference nestedSchemaRef) {
        return addRepeatingNamedAt(0, name, nestedSchemaRef);
    }

    public SchemaBuilder<S> addRepeatingNamedAt(int index,
                                             String name,
                                             SchemaReference nestedSchemaRef) {

        schemaFactory.addRepeatingNamedAt(index, name, nestedSchemaRef);
        return this;
    }

    public SchemaBuilder<S> removeAt(int index) {

        schemaFactory.removeAt(index);
        return this;

    }

    public SchemaBuilder<S> removeNamed(String name) {

        schemaFactory.removeNamed(name);
        return this;
    }

    /**
     * Merge a schema giving priority to the type of the priority schema.
     * If the name of the priority field exists in existing schema, the field is
     * replaced at the existing index. If the name of the priority field is not
     * in the existing schema it is added at the end. The indexes of the incoming
     * schema is ignored.
     *
     * @param prioritySchema The schema to take priority in the merge.
     * @return This builder.
     */
    public SchemaBuilder<S> merge(DataSchema prioritySchema) {

        schemaFactory.merge(prioritySchema);
        return this;
    }


    public S build() {
        return schemaFactory.toSchema();
    }

}
