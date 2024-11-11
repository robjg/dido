package dido.data;

import dido.data.util.DataBuilder;

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
                .addNamedAt(1, INDEX_FIELD, int.class)
                .addNamedAt(2, FIELD_FIELD, String.class)
                .addNamedAt(3, TYPE_FIELD, String.class)
                .addNestedNamedAt(4, NESTED_FIELD, schemaRef)
                .addNamedAt(5, REPEATING_FIELD, boolean.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addRepeatingNamed(FIELDS_FIELD, fieldSchema)
                .build();

        schemaRef.set(schema);

        DATA_SCHEMA_SCHEMA = schemaManager.getSchema(SCHEMA_SCHEMA_NAME);
    }

    public static DidoData schemaToData(DataSchema schema) {

        DataBuilder builder = MapData.builderForSchema(DATA_SCHEMA_SCHEMA);

        DataSchema fieldSchema = DATA_SCHEMA_SCHEMA.getSchemaNamed(FIELDS_FIELD);

        List<DidoData> fields = new ArrayList<>();


        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {

            SchemaField schemaField = schema.getSchemaFieldAt(index);

            DataBuilder fieldBuilder = MapData.builderForSchema(fieldSchema);

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

        RepeatingData fields = (RepeatingData) data.getNamed(FIELDS_FIELD);

        SchemaFactory builder = DataSchemaFactory.newInstance();

        for (DidoData fieldData : fields) {

            int index = fieldData.getIntNamed(INDEX_FIELD);
            String name = fieldData.getStringNamed(FIELD_FIELD);

            SchemaField schemaField;
            if (fieldData.hasNamed(NESTED_FIELD)) {

                DidoData nestedData = (DidoData) fieldData.getNamed(NESTED_FIELD);
                DataSchema nestedSchema = schemaFromData(nestedData, classLoader);

                if (fieldData.hasNamed(REPEATING_FIELD) && fieldData.getBooleanNamed(REPEATING_FIELD)) {
                    schemaField = SchemaField.ofRepeating(index, name, nestedSchema);
                } else {
                    schemaField = SchemaField.ofNested(index, name, nestedSchema);
                }
            } else {
                Class<?> type = classLoader.apply(fieldData.getStringNamed(TYPE_FIELD));
                schemaField = SchemaField.of(index, name, type);
            }
            builder.addSchemaField(schemaField);
        }

        return builder.toSchema();
    }

}
