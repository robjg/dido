package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataSchemaFactoryTest {

    @Test
    void whenAddSchemaFieldThenOk() {

        DataSchemaFactory simpleSchemaFactory = DataSchemaFactory.newInstance();
        simpleSchemaFactory.addSchemaField(SchemaField.of(5, "Foo", Integer.class));
        DataSchema schemaSimple = simpleSchemaFactory.toSchema();

        assertThat(schemaSimple.getSchemaFieldAt(5), is(SchemaField.of(5, "Foo", Integer.class)));

        DataSchemaFactory nestedSchemaFactory = DataSchemaFactory.newInstance();
        nestedSchemaFactory.addSchemaField(SchemaField.ofNested(3, "Nested", schemaSimple));
        DataSchema schemaNested = nestedSchemaFactory.toSchema();

        assertThat(schemaNested.getSchemaFieldAt(3), is(SchemaField.ofNested(3, "Nested", schemaSimple)));

        DataSchemaFactory repeatingSchemaFactory = DataSchemaFactory.newInstance();
        repeatingSchemaFactory.addSchemaField(SchemaField.ofRepeating(3, "Nested", schemaSimple));
        DataSchema schemaRepeating = repeatingSchemaFactory.toSchema();

        assertThat(schemaRepeating.getSchemaFieldAt(3), is(SchemaField.ofRepeating(3, "Nested", schemaSimple)));
    }

}
