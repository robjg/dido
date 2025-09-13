package dido.data.schema;

import dido.data.*;
import dido.data.util.ClassUtils;
import dido.data.util.DataBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Defines the {@link DataSchema} for a {@link DataSchema} Provides methods to and from
 * {@link DidoData}.
 */
public class DataSchemaSchema {

    public static final String SCHEMA_SCHEMA_NAME = "DataSchema";

    public static final String FIELD_SCHEMA_NAME = "FieldSchema";

    // Schema Def Field Names
    public static final String DEF_FIELD = "Def";

    public static final String SCHEMA_FIELD = "Schema";

    // Complete Schema Fields

    public static final String DEFS_FIELD = "Defs";

    // and Schema and Ref.

    // Schema Fields


    public static final String FIELDS_FIELD = "Fields";


    // Schema Field Fields
    public static final String INDEX_FIELD = "Index";

    public static final String NAME_FIELD = "Name";

    public static final String TYPE_FIELD = "Type";

    public static final String NESTED_FIELD = "Nested";

    public static final String REPEATING_FIELD = "Repeating";

    public static final String REF_FIELD = "Ref";

    public static final DataSchema DEFS_SCHEMA;

    public static final DataSchema NESTED_SCHEMA;

    public static final DataSchema FIELD_SCHEMA;

    public static final DataSchema MAIN_SCHEMA_SCHEMA;

    public static final DataSchema DATA_SCHEMA_SCHEMA;

    static {

        SchemaDefs defs = SchemaDefs.newInstance();

        DEFS_SCHEMA = SchemaBuilder.newInstance()
                .withSchemaDefs(defs)
                .addNamed(DEF_FIELD, String.class)
                .addRefNamed(SCHEMA_FIELD, SCHEMA_SCHEMA_NAME)
                .build();

        NESTED_SCHEMA = SchemaBuilder.newInstance()
                .withSchemaDefs(defs)
                .addNamed(REF_FIELD, String.class)
                .addRefNamed(SCHEMA_FIELD, SCHEMA_SCHEMA_NAME)
                .addNamed(REPEATING_FIELD, boolean.class)
                .build();

        FIELD_SCHEMA = SchemaBuilder.newInstance()
                .withSchemaDefs(defs)
                .withSchemaName(FIELD_SCHEMA_NAME)
                .addNamed(INDEX_FIELD, int.class)
                .addNamed(NAME_FIELD, String.class)
                .addNamed(TYPE_FIELD, String.class)
                .addNestedNamed(NESTED_FIELD, NESTED_SCHEMA)
                .build();

        MAIN_SCHEMA_SCHEMA = SchemaBuilder.newInstance()
                .withSchemaDefs(defs)
                .withSchemaName(SCHEMA_SCHEMA_NAME)
                .addRepeatingRefNamed(FIELDS_FIELD, FIELD_SCHEMA_NAME)
                .build();

        DATA_SCHEMA_SCHEMA = SchemaBuilder.newInstance()
                .withSchemaDefs(defs)
                .addNamed(NAME_FIELD, String.class)
                .addRepeatingNamed(DEFS_FIELD, DEFS_SCHEMA)
                .addRefNamed(SCHEMA_FIELD, SCHEMA_SCHEMA_NAME)
                .build();
    }

    public static DidoData schemaToData(DataSchema schema) {

        return new DataFromSchema().schemaToData(schema);
    }


    /**
     * Get a schema from data using a class loader.
     *
     * @param data        The data representing the schema.
     * @param classLoader The function that resolves any classes.
     * @return The schema.
     */
    public static DataSchema schemaFromData(DidoData data,
                                            ClassLoader classLoader) {

        return schemaFromData(data,
                className -> {
                    try {
                        return ClassUtils.classFor(className, classLoader);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Get a schema from data using a class resolver function. Primarily here for Oddjob.
     *
     * @param data              The data representing the schema.
     * @param classResolverFunc The function that resolves any classes.
     * @return The schema.
     */
    public static DataSchema schemaFromData(DidoData data,
                                            Function<? super String, ? extends Class<?>> classResolverFunc) {

        return new SchemaFromData(classResolverFunc).schemaFromData(data);
    }

    static class DataFromSchema {

        private final Map<String, DidoData> defs = new LinkedHashMap<>();

        private DataSchema startingSchema;

        private String ourRefName;

        DidoData schemaToData(DataSchema schema) {
            this.startingSchema = schema;

            DidoData schemaData = nestedSchemaToData(schema);

            DataBuilder builder = DidoData.builderForSchema(DATA_SCHEMA_SCHEMA);

            if (ourRefName != null) {
                builder.withString(NAME_FIELD, ourRefName);
            }

            if (!defs.isEmpty()) {
                List<DidoData> defList = new ArrayList<>();
                DataBuilder defBuilder = DidoData.builderForSchema(DEFS_SCHEMA);
                for (Map.Entry<String, DidoData> defEntry : defs.entrySet()) {
                    defBuilder.withString(DEF_FIELD, defEntry.getKey());
                    defBuilder.with(SCHEMA_FIELD, defEntry.getValue());
                    defList.add(defBuilder.build());
                }
                builder.with(DEFS_FIELD, RepeatingData.of(defList));
            }

            builder.with(SCHEMA_FIELD, schemaData);

            return builder.build();
        }

        DidoData nestedSchemaToData(DataSchema schema) {

            DataBuilder builder = DidoData.builderForSchema(MAIN_SCHEMA_SCHEMA);

            List<DidoData> fields = new ArrayList<>();

            DataFactory fieldFactory = DidoData.factoryForSchema(FIELD_SCHEMA);
            WritableData fieldData = fieldFactory.getWritableData();

            DataFactory nestedFactory = DidoData.factoryForSchema(NESTED_SCHEMA);
            WritableData nestedData = nestedFactory.getWritableData();

            for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {

                SchemaField schemaField = schema.getSchemaFieldAt(index);

                fieldData.setIntNamed(INDEX_FIELD, index);
                fieldData.setStringNamed(NAME_FIELD, schemaField.getName());
                fieldData.setStringNamed(TYPE_FIELD, schemaField.getType().getTypeName());

                if (schemaField.isNested()) {
                    nestedData.setBooleanNamed(REPEATING_FIELD, schemaField.isRepeating());
                    DataSchema nestedSchema = schemaField.getNestedSchema();
                    if (schemaField instanceof SchemaField.Ref ref) {
                        String refSchemaName = ref.getSchemaName();
                        nestedData.setStringNamed(REF_FIELD, refSchemaName);
                        if (startingSchema == nestedSchema) {
                            ourRefName = refSchemaName;
                        } else {
                            if (!defs.containsKey(refSchemaName)) {
                                // Stops recursion when same name appears down the tree.
                                defs.put(refSchemaName, DidoData.of());
                                DidoData nestedSchemaAsData = nestedSchemaToData(nestedSchema);
                                defs.put(refSchemaName, nestedSchemaAsData);
                            }
                        }
                    } else {
                        nestedData.setNamed(SCHEMA_FIELD, nestedSchemaToData(nestedSchema));
                    }
                    fieldData.setNamed(NESTED_FIELD, nestedFactory.toData());
                }

                fields.add(fieldFactory.toData());
            }

            builder.with(FIELDS_FIELD, RepeatingData.of(fields));

            return builder.build();
        }

    }

    static class SchemaFromData {

        private final Function<? super String, ? extends Class<?>> classResolverFunc;

        SchemaFromData(Function<? super String, ? extends Class<?>> classResolverFunc) {
            this.classResolverFunc = classResolverFunc;
        }

        DataSchema schemaFromData(DidoData data) {

            SchemaDefs defs = null;

            if (data.hasNamed(DEFS_FIELD)) {

                defs = SchemaDefs.newInstance();

                for (DidoData defData : ((RepeatingData) data.getNamed(DEFS_FIELD))) {

                    DataSchema defSchema = nestedSchemaFromData((DidoData) defData.getNamed(SCHEMA_FIELD), defs);
                    defs.setSchema(defData.getStringNamed(DEF_FIELD), defSchema);
                }
            }

            String schemaName = null;
            if (data.hasNamed(NAME_FIELD)) {
                schemaName = data.getStringNamed(REF_FIELD);
                if (defs == null) {
                    defs = SchemaDefs.newInstance();
                }
            }

            DataSchema schema = nestedSchemaFromData((DidoData) data.getNamed(SCHEMA_FIELD), defs);

            if (schemaName != null) {
                defs.setSchema(schemaName, schema);
            }

            return schema;
        }

        DataSchema nestedSchemaFromData(DidoData data, SchemaDefs defs) {

            RepeatingData fields = (RepeatingData) data.getNamed(FIELDS_FIELD);

            SchemaFactory builder = DataSchemaFactory.newInstance();

            for (DidoData fieldData : fields) {

                int index = fieldData.getIntNamed(INDEX_FIELD);
                String name = fieldData.getStringNamed(NAME_FIELD);

                SchemaField schemaField;
                if (fieldData.hasNamed(NESTED_FIELD)) {

                    DidoData nestedData = (DidoData) fieldData.getNamed(NESTED_FIELD);

                    boolean repeating = nestedData.hasNamed(REPEATING_FIELD) &&
                            nestedData.getBooleanNamed(REPEATING_FIELD);

                    if (nestedData.hasNamed(REF_FIELD)) {
                        String refSchemaName = nestedData.getStringNamed(REF_FIELD);
                        schemaField = repeating ?
                                SchemaField.ofRepeatingRef(index, name, refSchemaName)
                                        .toSchemaField(defs) :
                                SchemaField.ofRef(index, name, refSchemaName)
                                        .toSchemaField(defs);
                    } else {
                        DidoData nestedSchemaData = (DidoData) nestedData.getNamed(SCHEMA_FIELD);

                        schemaField = repeating ? SchemaField.ofRepeating(index, name,
                                nestedSchemaFromData(nestedSchemaData, defs)) :
                                SchemaField.ofNested(index, name,
                                        nestedSchemaFromData(nestedSchemaData, defs));
                    }
                } else {
                    Class<?> type = classResolverFunc.apply(fieldData.getStringNamed(TYPE_FIELD));
                    schemaField = SchemaField.of(index, name, type);
                }
                builder.addSchemaField(schemaField);
            }

            return builder.toSchema();
        }
    }

}
