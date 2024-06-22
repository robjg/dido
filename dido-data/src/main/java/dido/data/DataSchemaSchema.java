package dido.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * {@link DataSchema} to and from {@link DidoData}. Doesn't cope with recursive schemas yet.
 */
public class DataSchemaSchema {

    public static final String SCHEMA_SCHEMA_NAME = "DataSchema";

    public static final String FIELD_SCHEMA_NAME = "SchemaField";

    public static final String INDEX_FIELD = "Index";

    public static final String FIELD_FIELD = "Field";

    public static final String TYPE_FIELD = "Type";

    public static final String NESTED_FIELD = "Nested";

    public static final String REPEATING_FIELD = "Repeating";

    public static final String FIELDS_FIELD = "Fields";

    public static DataSchema DATA_SCHEMA_SCHEMA;

    static {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newSchema(FIELD_SCHEMA_NAME)
                .addField(INDEX_FIELD, int.class)
                .addField(FIELD_FIELD, String.class)
                .addField(TYPE_FIELD, String.class)
                .addNestedField(NESTED_FIELD, SCHEMA_SCHEMA_NAME)
                .addField(REPEATING_FIELD, boolean.class)
                .addToManager()
                .newSchema(SCHEMA_SCHEMA_NAME)
                .addRepeatingField(FIELDS_FIELD, FIELD_SCHEMA_NAME)
                .addToManager();

        SchemaReference schemaRef = SchemaReference.blank();

        DataSchema fieldSchema = SchemaBuilder.newInstance()
                .addSchemaField(SchemaFields.of(1, INDEX_FIELD, int.class))
                .addSchemaField(SchemaFields.of(2, FIELD_FIELD, String.class))
                .addSchemaField(SchemaFields.of(3, TYPE_FIELD, String.class))
                .addSchemaField(SchemaFields.ofNested(4, NESTED_FIELD, schemaRef))
                .addSchemaField(SchemaFields.of(5, REPEATING_FIELD, boolean.class))
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addRepeatingField(FIELDS_FIELD, fieldSchema)
                .build();

        schemaRef.set(schema);

        DATA_SCHEMA_SCHEMA = schemaManager.getSchema(SCHEMA_SCHEMA_NAME);
    }

    public static DidoData schemaToData(DataSchema schema) {

        NamedDataBuilder builder = MapData.newBuilder(DATA_SCHEMA_SCHEMA);

        DataSchema fieldSchema = DATA_SCHEMA_SCHEMA.getSchemaNamed(FIELDS_FIELD);

        List<DidoData> fields = new ArrayList<>();


        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {

            SchemaField schemaField = schema.getSchemaFieldAt(index);

            NamedDataBuilder fieldBuilder = MapData.newBuilder(fieldSchema);

            fieldBuilder.withInt(INDEX_FIELD, index);
            if (schemaField.getName() != null) {
                fieldBuilder.withString(FIELD_FIELD, schemaField.getName());
            }
            if (schemaField.isNested()) {
                fieldBuilder.with(NESTED_FIELD, schemaToData(schemaField.getNestedSchema()));
            } else {
                fieldBuilder.with(TYPE_FIELD, schemaField.getType().getName());
            }

            if (schemaField.isRepeating()) {
                fieldBuilder.withBoolean(REPEATING_FIELD, true);
            }

            fields.add(fieldBuilder.build());
        }

        builder.with(FIELDS_FIELD, RepeatingData.of(fields));

        return builder.build();
    }

    public static DataSchema schemaFromData(DidoData data,
                                            Function<? super String, ? extends Class<?>> classLoader) {

        RepeatingData fields = data.getNamedAs(FIELDS_FIELD, RepeatingData.class);

        SchemaBuilder builder = SchemaBuilder.newInstance();

        for (DidoData fieldData : fields) {

            int index = fieldData.getIntNamed(INDEX_FIELD);
            String field = fieldData.getStringNamed(FIELD_FIELD);

            if (fieldData.hasNamed(NESTED_FIELD)) {

                DidoData nestedData = fieldData.getNamedAs(NESTED_FIELD, DidoData.class);
                DataSchema nestedSchema = schemaFromData(nestedData, classLoader);

                if (fieldData.hasNamed(REPEATING_FIELD) && fieldData.getBooleanNamed(REPEATING_FIELD)) {
                    builder.addRepeatingFieldAt(index, field, nestedSchema);
                } else {
                    builder.addNestedFieldAt(index, field, nestedSchema);
                }
            } else {
                Class<?> type = classLoader.apply(fieldData.getStringNamed(TYPE_FIELD));

                builder.addFieldAt(index, field, type);
            }
        }

        return builder.build();
    }

}
