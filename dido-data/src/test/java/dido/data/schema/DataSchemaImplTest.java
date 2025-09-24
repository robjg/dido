package dido.data.schema;

import dido.data.DataSchema;
import dido.data.SchemaField;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class DataSchemaImplTest {

    @Test
    void basics() {

        DataSchema schema = DataSchemaImpl.fromFields(
                SchemaField.of(2, "Fruit", String.class),
                SchemaField.of(5, "Qty", int.class),
                SchemaField.of(7, "Price", double.class));

        assertThat(schema.getSize(), is(3));
        assertThat(schema.firstIndex(), is(2));
        assertThat(schema.lastIndex(), is(7));
        assertThat(schema.getIndices(), is(new int[]{2, 5, 7}));

        assertThat(schema.hasIndex(3), is(false));
        assertThat(schema.hasIndex(5), is(true));

        assertThat(schema.getTypeAt(5), is(int.class));
        assertThat(schema.getTypeNamed("Price"), is(double.class));

        assertThat(schema.getSchemaFieldAt(2),
                is(SchemaField.of(2, "Fruit", String.class)));
        assertThat(schema.getSchemaFieldAt(3),
                is(nullValue()));

        assertThat(schema.getSchemaFieldNamed("Price"),
                is(SchemaField.of(7, "Price", double.class)));
        assertThat(schema.getSchemaFieldNamed("Alice"),
                is(nullValue()));
    }

    @Test
    void recursive() {

        SchemaDefs defs = SchemaDefs.newInstance();

        DataSchema schema = DataSchemaImpl.fromFields(
                SchemaField.of(1, "Name", String.class),
                SchemaField.ofRepeatingRef(2, "Children", "person").toSchemaField(defs));

        defs.setSchema("person", schema);

        assertThat(schema, is(schema));

        assertThat(schema.toString(),
                is("{[1:Name]=java.lang.String, [2:Children]=[Ref#person]}"));

    }

}