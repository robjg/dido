package dido.data.schema;

import dido.data.DataSchema;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SchemaDefsImplTest {

    @Test
    void refGetThenSet() {

        SchemaDefs defs = new SchemaDefsImpl();

        SchemaRef ref = defs.getSchemaRef("Foo");

        assertThat(ref.isResolved(), is(false));

        DataSchema schema = DataSchema.emptySchema();

        defs.setSchema("Foo", schema);

        assertThat(ref.isResolved(), is(true));
        assertThat(ref.get(), is(schema));
    }

    @Test
    void refSetThenGet() {

        SchemaDefs defs = new SchemaDefsImpl();

        DataSchema schema = DataSchema.emptySchema();

        defs.setSchema("Foo", schema);

        SchemaRef ref = defs.getSchemaRef("Foo");

        assertThat(ref.isResolved(), is(true));
        assertThat(ref.get(), is(schema));
    }
}