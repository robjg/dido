package dido.json;

import dido.data.DataSchema;
import dido.data.SchemaManager;
import dido.how.CloseableConsumer;
import dido.how.CloseableSupplier;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

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

    @Test
    void givenBasicSchemaCanSerialiseToAndFromStream() throws Exception {

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

        CloseableConsumer<DataSchema<String>> consumer = SchemaAsJson.toJsonStream(out);

        consumer.accept(schema);
        consumer.accept(schema);
        consumer.accept(schema);

        consumer.close();

        String jsonString = out.toString();

        logger.info(jsonString);

        ByteArrayInputStream input = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

        CloseableSupplier<DataSchema<String>> supplier = SchemaAsJson.fromJsonStream(input);

        DataSchema<String> back1 = supplier.get();
        DataSchema<String> back2 = supplier.get();
        DataSchema<String> back3 = supplier.get();
        DataSchema<String> back4 = supplier.get();

        supplier.close();

        assertThat(back1, is(schema));
        assertThat(back2, is(schema));
        assertThat(back3, is(schema));
        assertThat(back4, nullValue());
    }

}
