package dido.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * {@link DataSchema} to and from {@link DidoData}. Doesn't cope with recursive schemas yet.
 */
public class DataSchemaSchema  {

    public static final String SCHEMA_SCHEMA_NAME = "DataSchema";

    public static final String FIELD_SCHEMA_NAME = "SchemaField";

    public static final String INDEX_FIELD = "Index";

    public static final String FIELD_FIELD = "Field";

    public static final String TYPE_FIELD = "Type";

    public static final String NESTED_FIELD = "Nested";

    public static final String REPEATING_FIELD = "Repeating";

    public static final String FIELDS_FIELD = "Fields";

    public static DataSchema DATA_SCHEMA_SCHEMA;

    static  {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newSchema(FIELD_SCHEMA_NAME, String.class)
                .addField(INDEX_FIELD, int.class)
                .addField(FIELD_FIELD, String.class)
                .addField(TYPE_FIELD, String.class)
                .addNestedField(NESTED_FIELD, SCHEMA_SCHEMA_NAME)
                .addField(REPEATING_FIELD, boolean.class)
                .addToManager()
                .newSchema(SCHEMA_SCHEMA_NAME, String.class)
                .addRepeatingField(FIELDS_FIELD, FIELD_SCHEMA_NAME)
                .addToManager();

        SchemaReference<String> schemaRef = SchemaReference.blank();

        GenericDataSchema<String> fieldSchema = SchemaBuilder.forStringFields()
                .addGenericSchemaField(GenericSchemaFields.of(1, INDEX_FIELD, int.class))
                .addGenericSchemaField(GenericSchemaFields.of(2, FIELD_FIELD, String.class))
                .addGenericSchemaField(GenericSchemaFields.of(3, TYPE_FIELD, String.class))
                .addGenericSchemaField(GenericSchemaFields.ofNested(4, NESTED_FIELD, schemaRef))
                .addGenericSchemaField(GenericSchemaFields.of(5, REPEATING_FIELD, boolean.class))
                .build();

        GenericDataSchema<String> schema = SchemaBuilder.forStringFields()
                .addRepeatingField(FIELDS_FIELD, fieldSchema)
                .build();

        schemaRef.set(schema);

        DATA_SCHEMA_SCHEMA = schemaManager.getSchema(SCHEMA_SCHEMA_NAME);
    }

    public static DidoData schemaToData(DataSchema schema) {

        DataBuilder builder = MapData.newBuilder(DATA_SCHEMA_SCHEMA);

        DataSchema fieldSchema = DATA_SCHEMA_SCHEMA.getSchemaNamed(FIELDS_FIELD);

        List<DidoData> fields = new ArrayList<>();


        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {

            SchemaField schemaField = schema.getSchemaFieldAt(index);

            DataBuilder fieldBuilder = MapData.newBuilder(fieldSchema);

            fieldBuilder.setInt(INDEX_FIELD, index);
            if (schemaField.getName() != null) {
                fieldBuilder.setString(FIELD_FIELD, schemaField.getName());
            }
            if (schemaField.isNested()) {
                fieldBuilder.set(NESTED_FIELD, schemaToData(schemaField.getNestedSchema()));
            }
            else {
                fieldBuilder.set(TYPE_FIELD, schemaField.getType().getName());
            }

            if (schemaField.isRepeating()) {
                fieldBuilder.setBoolean(REPEATING_FIELD, true);
            }

            fields.add(fieldBuilder.build());
        }

        builder.set(FIELDS_FIELD, RepeatingData.of(fields));

        return builder.build();
    }

    public static GenericDataSchema<String> schemaFromData(DidoData data,
                                                           Function<? super String, ? extends Class<?>> classLoader) {

        RepeatingData fields = data.getAs(FIELDS_FIELD, RepeatingData.class);

        SchemaBuilder<String> builder = SchemaBuilder.forStringFields();

        for (DidoData fieldData : fields) {

            int index = fieldData.getInt(INDEX_FIELD);
            String field = fieldData.getString(FIELD_FIELD);

            if (fieldData.hasField(NESTED_FIELD)) {

                DidoData nestedData = fieldData.getAs(NESTED_FIELD, DidoData.class);
                GenericDataSchema<String> nestedSchema = schemaFromData(nestedData, classLoader);

                if (fieldData.hasField(REPEATING_FIELD) && fieldData.getBoolean(REPEATING_FIELD)) {
                    builder.addRepeatingFieldAt(index, field, nestedSchema);
                }
                else {
                    builder.addNestedFieldAt(index, field, nestedSchema);
                }
            }
            else {
                Class<?> type = classLoader.apply(fieldData.getString(TYPE_FIELD));

                builder.addFieldAt(index, field, type);
            }
        }

        return builder.build();
    }

}
