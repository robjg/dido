package dido.json;

import dido.data.DataSchema;
import dido.data.SchemaManager;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SchemaAsJsonTest {

    private static final Logger logger = LoggerFactory.getLogger(SchemaAsJsonTest.class);

    @Test
    void givenBasicSchemaCanSerialiseToAndFromJson() throws Exception {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newDefaultSchema(String.class)
                .addField("Name", String.class)
                .addRepeatingField("Hobbies", String.class)
                .addField("Title", String.class)
                .addField("Cost", double.class)
                .addBack()
                .addToManager();

        DataSchema<String> schema = schemaManager.getDefaultSchema();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SchemaAsJson.toJson(schema, out);

        String jsonString = out.toString();

        logger.info(jsonString);

        ByteArrayInputStream input = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

        DataSchema<String> back = SchemaAsJson.fromJson(input);

        assertThat(back, is(schema));

    }

}
