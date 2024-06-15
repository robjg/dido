package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class GenericDataSchemaTest {

    @Test
    public void testEmptySchema() {

        GenericDataSchema<String> schema = GenericDataSchema.emptySchema();

        assertThat(schema.firstIndex(), is(0));
        assertThat(schema.lastIndex(), is(0));
        assertThat(schema.nextIndex(0), is(0));
        assertThat(schema.getFields(), empty());
    }

    @Test
    public void testEmptySchemaEquality() {

        GenericDataSchema<String> schema1 = GenericDataSchema.emptySchema();

        GenericDataSchema<String> schema2 = GenericDataSchema.emptySchema();

        GenericDataSchema<Integer> schema3 = GenericDataSchema.emptySchema();

        assertThat(schema1.hashCode(), is(0));

        assertThat(schema1, is(schema2));
        assertThat(schema1.hashCode(), is(schema2.hashCode()));
        assertThat(schema2, is(schema3));
        assertThat(schema2.hashCode(), is(schema3.hashCode()));
    }

}
