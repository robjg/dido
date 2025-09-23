package dido.data.schema;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.data.immutable.ArrayData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SubSchemaTest {

    @Test
    void byIndex() {

        DataSchema dataSchema = ArrayData.schemaBuilder()
                .addNamed("Id", String.class)
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Colour", String.class)
                .build();

        DataSchema subSchema = SubSchema.from(dataSchema).withIndices(1, 4);

        assertThat(subSchema.firstIndex(), is(1));
        assertThat(subSchema.nextIndex(1), is(4));
        assertThat(subSchema.nextIndex(4), is(0));
        assertThat(subSchema.lastIndex(), is(4));

        assertThat(subSchema.getSchemaFieldAt(1),
                is(SchemaField.of(1, "Id", String.class)));
        assertThat(subSchema.getSchemaFieldAt(3), nullValue());

        assertThat(subSchema.getFieldNameAt(4), is("Price"));
        assertThat(subSchema.getFieldNameAt(7), nullValue());

        assertThat(subSchema.getIndexNamed("Id"), is(1));
        assertThat(subSchema.getIndexNamed("Bob"), is(0));

        DataSchema expectedSchema = ArrayData.schemaBuilder()
                .addNamed("Id", String.class)
                .addNamedAt(4, "Price", double.class)
                .build();

        assertThat(subSchema, is(expectedSchema));

        assertThat(subSchema.toString(), is("{[1:Id]=java.lang.String, [4:Price]=double}"));
    }

}