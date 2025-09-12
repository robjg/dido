package dido.data.schema;

import dido.data.DataSchema;
import dido.data.DidoData;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

class DataSchemaSchemaTest {

    private static final Logger logger = LoggerFactory.getLogger(DataSchemaSchemaTest.class);

    @Test
    void givenSimpleSchemaThenCanGoToDataAndBack() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("type", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DidoData data = DataSchemaSchema.schemaToData(schema);

        logger.info(data.toString());

        DataSchema back = DataSchemaSchema.schemaFromData(data,
                className -> {
                    try {
                        return ClassUtils.classFor(className, getClass().getClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

        assertThat(back, is(schema));
    }

    @Test
    void givenSchemaSchemaThenCanGoToDataAndBack() {

        DidoData data = DataSchemaSchema.schemaToData(DataSchemaSchema.DATA_SCHEMA_SCHEMA);

        logger.info(data.toString());

        DataSchema back = DataSchemaSchema.schemaFromData(data, getClass().getClassLoader());

        assertThat(back, is(DataSchemaSchema.DATA_SCHEMA_SCHEMA));

        DataSchema mainSchema = back.getSchemaNamed(DataSchemaSchema.SCHEMA_FIELD);

        DataSchema fieldsSchema = mainSchema.getSchemaNamed(DataSchemaSchema.FIELDS_FIELD);

        DataSchema nestedSchema = fieldsSchema.getSchemaNamed(DataSchemaSchema.NESTED_FIELD);

        DataSchema nestedSchemaSchema = nestedSchema.getSchemaNamed(DataSchemaSchema.SCHEMA_FIELD);

        assertThat(nestedSchemaSchema, sameInstance(mainSchema));
    }
}