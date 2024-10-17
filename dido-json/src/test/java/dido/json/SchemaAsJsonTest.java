package dido.json;

import dido.data.DataSchema;
import dido.data.SchemaManager;
import dido.how.CloseableConsumer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class SchemaAsJsonTest {

    private static final Logger logger = LoggerFactory.getLogger(SchemaAsJsonTest.class);

    @Test
    void givenBasicSchemaCanSerialiseToAndFromJson() throws Exception {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newDefaultSchema()
                .addField("Name", String.class)
                .addRepeatingField("Hobbies")
                .addField("Title", String.class)
                .addField("Cost", double.class)
                .addBack()
                .addToManager();

        DataSchema schema = schemaManager.getDefaultSchema();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SchemaAsJson.toJson(schema, out);

        String jsonString = out.toString();

        logger.info(jsonString);

        ByteArrayInputStream input = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

        DataSchema back = SchemaAsJson.fromJson(input);

        assertThat(back, is(schema));

    }

    @Test
    void givenBasicSchemaCanSerialiseToAndFromStream() throws Exception {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newDefaultSchema()
                .addField("Name", String.class)
                .addRepeatingField("Hobbies")
                .addField("Title", String.class)
                .addField("Cost", double.class)
                .addBack()
                .addToManager();

        DataSchema schema = schemaManager.getDefaultSchema();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        CloseableConsumer<DataSchema> consumer = SchemaAsJson.toJsonStream(out);

        consumer.accept(schema);
        consumer.accept(schema);
        consumer.accept(schema);

        consumer.close();

        String jsonString = out.toString();

        logger.info(jsonString);

        ByteArrayInputStream input = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

        List<DataSchema> back = SchemaAsJson.fromJsonStream(input)
                .collect(Collectors.toList());

        assertThat(back, contains(schema, schema, schema));
    }

}
