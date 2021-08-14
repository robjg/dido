package dido.data;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataSchemaTest {

    @Test
    public void testEmptySchema() {

        DataSchema<String> schema = DataSchema.emptyStringFieldSchema();

        assertThat(schema.getFieldType(), is(String.class));
        assertThat(schema.firstIndex(), is(0));
        assertThat(schema.lastIndex(), is(0));
        assertThat(schema.nextIndex(0), is(0));
        assertThat(schema.getFields().isEmpty(), is(true));
    }

    @Test
    public void testEmptySchemaEquality() {

        DataSchema<String> schema1 = DataSchema.emptyStringFieldSchema();

        DataSchema<String> schema2 = DataSchema.emptySchema(String.class);

        DataSchema<Integer> schema3 = DataSchema.emptySchema(Integer.class);

        assertThat(schema1, is(schema2));
        assertThat(schema1.hashCode(), is(schema2.hashCode()));
        assertThat(schema2, Matchers.not(schema3));
    }
}
