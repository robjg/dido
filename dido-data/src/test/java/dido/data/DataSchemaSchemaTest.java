package dido.data;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataSchemaSchemaTest {

    private static final Logger logger = LoggerFactory.getLogger(DataSchemaSchemaTest.class);

    @Test
    void givenSimpleSchemaThenCanGoToDataAndBack() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("type", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        GenericData<String> data = DataSchemaSchema.schemaToData(schema);

        logger.info(data.toString());

        DataSchema<String> back = DataSchemaSchema.schemaFromData(data,
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
    void givenRepeatingSchemaThenCanGoToDataAndBack() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newDefaultSchema(String.class)
                .addField("Name", String.class)
                .addRepeatingField("Hobbies", String.class)
                .addField("Title", String.class)
                .addField("Cost", double.class)
                .addBack()
                .addToManager();

        DataSchema<String> schema = schemaManager.getDefaultSchema();

        GenericData<String> data = DataSchemaSchema.schemaToData(schema);

        logger.info(data.toString());

        DataSchema<String> back = DataSchemaSchema.schemaFromData(data,
                className -> {
                    try {
                        return ClassUtils.classFor(className, getClass().getClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

        assertThat(back, is(schema));
    }
}