package dido.data;

/**
 * Builder for an {@link DataSchema}.
 */
public class SchemaBuilder {

    private final SchemaFactory schemaFactory;

    private SchemaBuilder(SchemaFactory schemaFactory) {
        this.schemaFactory = schemaFactory;
    }

    public static SchemaBuilder builderFor(SchemaFactory schemaFactory) {
        return new SchemaBuilder(schemaFactory);
    }

    public static SchemaBuilder newInstance() {
        return new SchemaBuilder(DataSchemaFactory.newInstance());
    }

    // Add Simple Fields

    public SchemaBuilder add(Class<?> fieldType) {
        return addAt(0, fieldType);
    }

    public SchemaBuilder addAt(int index, Class<?> fieldType) {
        return addNamedAt(index, null, fieldType);
    }

    public SchemaBuilder addNamed(String name, Class<?> fieldType) {
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
    public SchemaBuilder addNamedAt(int index, String name, Class<?> fieldType) {

        schemaFactory.addSchemaField(SchemaField.of(index, name, fieldType));
        return this;
    }

    // Add Nested Field

    public SchemaBuilder addNested(DataSchema nestedSchema) {
        return addNestedAt(0, nestedSchema);
    }

    public SchemaBuilder addNestedAt(int index,
                                     DataSchema nestedSchema) {
        return addNestedNamedAt(index, null, nestedSchema);
    }

    public SchemaBuilder addNestedNamed(String name,
                                        DataSchema nestedSchema) {
        return addNestedNamedAt(0, name, nestedSchema);
    }

    public SchemaBuilder addNestedNamedAt(int index,
                                          String name,
                                          DataSchema nestedSchema) {

        schemaFactory.addSchemaField(SchemaField.ofNested(index, name, nestedSchema));
        return this;
    }

    // Add Nested Reference

    public SchemaBuilder addNested(SchemaReference nestedSchemaRef) {
        return addNestedAt(0, nestedSchemaRef);
    }

    public SchemaBuilder addNestedAt(int index,
                                     SchemaReference nestedSchemaRef) {
        return addNestedNamedAt(index, null, nestedSchemaRef);
    }

    public SchemaBuilder addNestedNamed(String field,
                                        SchemaReference nestedSchemaRef) {
        return addNestedNamedAt(0, field, nestedSchemaRef);
    }

    public SchemaBuilder addNestedNamedAt(int index,
                                          String name,
                                          SchemaReference nestedSchemaRef) {

        schemaFactory.addSchemaField(SchemaField.ofNested(index, name, nestedSchemaRef));
        return this;
    }

    // Add Repeating Nested Schema

    public SchemaBuilder addRepeating(DataSchema nestedSchema) {
        return addRepeatingAt(0, nestedSchema);
    }

    public SchemaBuilder addRepeatingAt(int index,
                                        DataSchema nestedSchema) {
        return addRepeatingNamedAt(index, null, nestedSchema);
    }

    public SchemaBuilder addRepeatingNamed(String name,
                                           DataSchema nestedSchema) {
        return addRepeatingNamedAt(0, name, nestedSchema);
    }

    public SchemaBuilder addRepeatingNamedAt(int index,
                                             String name,
                                             DataSchema nestedSchema) {
        schemaFactory.addSchemaField(SchemaField.ofRepeating(index, name, nestedSchema));
        return this;
    }

    // Add Repeating Nested Schema Ref

    public SchemaBuilder addRepeating(SchemaReference nestedSchemaRef) {
        return addRepeatingAt(0, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingAt(int index,
                                        SchemaReference nestedSchemaRef) {
        return addRepeatingNamedAt(index, null, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingNamed(String name,
                                           SchemaReference nestedSchemaRef) {
        return addRepeatingNamedAt(0, name, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingNamedAt(int index,
                                             String name,
                                             SchemaReference nestedSchemaRef) {

        schemaFactory.addSchemaField(SchemaField.ofRepeating(index, name, nestedSchemaRef));
        return this;
    }

    public SchemaBuilder removeAt(int index) {

        schemaFactory.removeAt(index);
        return this;

    }

    public SchemaBuilder removeNamed(String name) {

        schemaFactory.removeNamed(name);
        return this;
    }

    public SchemaBuilder addSchemaField(SchemaField schemaField) {

        schemaFactory.addSchemaField(schemaField);
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
    public SchemaBuilder merge(DataSchema prioritySchema) {

        schemaFactory.merge(prioritySchema);
        return this;
    }


    public DataSchema build() {

        return schemaFactory.toSchema();
    }

}
