package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataSchemaTest {

    @Test
    public void testEmptySchema() {

        DataSchema<String> schema = DataSchema.emptySchema();

        assertThat(schema.firstIndex(), is(0));
        assertThat(schema.lastIndex(), is(0));
        assertThat(schema.nextIndex(0), is(0));
        assertThat(schema.getFields().isEmpty(), is(true));
    }

    @Test
    public void testEmptySchemaEquality() {

        DataSchema<String> schema1 = DataSchema.emptySchema();

        DataSchema<String> schema2 = DataSchema.emptySchema();

        DataSchema<Integer> schema3 = DataSchema.emptySchema();

        assertThat(schema1.hashCode(), is(0));

        assertThat(schema1, is(schema2));
        assertThat(schema1.hashCode(), is(schema2.hashCode()));
        assertThat(schema2, is(schema3));
        assertThat(schema2.hashCode(), is(schema3.hashCode()));
    }

    @Test
    public void testEqualsDifferentLengths() {

        DataSchema<?> schema1 = mock(DataSchema.class);
        DataSchema<?> schema2 = mock(DataSchema.class);

        when(schema1.firstIndex()).thenReturn(2);
        when(schema1.nextIndex(2)).thenReturn(4);
        when(schema1.nextIndex(4)).thenReturn(0);

        when(schema2.firstIndex()).thenReturn(2);
        when(schema2.nextIndex(2)).thenReturn(4);
        when(schema2.nextIndex(4)).thenReturn(5);

        assertThat(DataSchema.equals(schema1, schema2), is(false));
    }
}
