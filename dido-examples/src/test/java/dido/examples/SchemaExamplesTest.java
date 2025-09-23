package dido.examples;

import dido.data.DataSchema;
import dido.data.schema.DataSchemaSchema;
import dido.json.SchemaAsJson;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SchemaExamplesTest {

    @Test
    void schemaAsJson() throws Exception {

        DataSchema back = SchemaAsJson.fromJson(
                getClass().getResourceAsStream("/schema/SchemaAsJson.json")
        );

        assertThat(back, is(DataSchemaSchema.DATA_SCHEMA_SCHEMA));

    }
}
