package dido.data;

import dido.data.schema.SchemaBuilder;
import dido.data.schema.SchemaDefs;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SchemaFieldTest {

    @Test
    void testToString() {

        assertThat(SchemaField.of(5, "Foo", Object.class).toString(),
                is("[5:Foo]=java.lang.Object"));

        DataSchema nested = SchemaBuilder.newInstance().
                addNamed("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, "Foo", nested).toString(),
                is("[5:Foo]={[1:Nested]=int}"));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested).toString(),
                is("[5:Foo]=[{[1:Nested]=int}]"));

        SchemaField.RefFactory ref = SchemaField.ofRef(5, "Foo", "SomeSchema");
        assertThat(ref.toString(),
                is("RefFactory{5:Foo]=SomeSchema}"));

        SchemaField.RefFactory repeatingRef = SchemaField.ofRepeatingRef(5, "Foo", "SomeSchema");
        assertThat(repeatingRef.toString(),
                is("RefFactory{5:Foo]=[SomeSchema]}"));

        SchemaDefs defs = SchemaDefs.newInstance();

        SchemaField refField = ref.toSchemaField(defs);
        assertThat(refField.toString(),
                is("[5:Foo]=SchemaReference{'SomeSchema'} (unset)"));

        SchemaField repeatingRefField = repeatingRef.toSchemaField(defs);
        assertThat(repeatingRefField.toString(),
                is("[5:Foo]=[SchemaReference{'SomeSchema'} (unset)]"));

        defs.setSchema("SomeSchema", DataSchema.emptySchema());

        assertThat(refField.toString(),
                is("[5:Foo]=SchemaReference{'SomeSchema'}"));

        assertThat(repeatingRefField.toString(),
                is("[5:Foo]=[SchemaReference{'SomeSchema'}]"));

    }

    @Test
    void testHashCode() {

        assertThat(SchemaField.of(5, "Foo", Object.class).hashCode(),
                is(SchemaField.of(5, "Foo", Object.class).hashCode()));

        DataSchema nested = SchemaBuilder.newInstance().
                addNamed("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, "Foo", nested).hashCode(),
                is(SchemaField.ofNested(5, "Foo", nested).hashCode()));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested).hashCode(),
                is(SchemaField.ofRepeating(5, "Foo", nested).hashCode()));

        SchemaDefs defs = SchemaDefs.newInstance();

        assertThat(SchemaField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).hashCode(),
                is(5));

        assertThat(SchemaField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).hashCode(),
                is(5));
    }

    @Test
    void testEquals() {

        assertThat(SchemaField.of(5, "Foo", Object.class),
                is(SchemaField.of(5, "Foo", Object.class)));

        DataSchema nested = SchemaBuilder.newInstance().
                addNamed("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, "Foo", nested),
                is(SchemaField.ofNested(5, "Foo", nested)));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested),
                is(SchemaField.ofRepeating(5, "Foo", nested)));

        SchemaDefs defs = SchemaDefs.newInstance();

        assertThat(SchemaField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs),
                is(SchemaField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs)));

        assertThat(SchemaField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs),
                is(SchemaField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs)));
    }

    @Test
    void testTypes() {

        assertThat(SchemaField.of(5, "Foo", Object.class).getType(),
                is(Object.class));

        DataSchema nested = SchemaBuilder.newInstance().
                addNamed("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, "Foo", nested).getType(),
                is(SchemaField.NESTED_TYPE));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested).getType(),
                is(SchemaField.NESTED_REPEATING_TYPE));

        SchemaDefs defs = SchemaDefs.newInstance();

        assertThat(SchemaField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).getType(),
                is(SchemaField.NESTED_TYPE));

        assertThat(SchemaField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).getType(),
                is(SchemaField.NESTED_REPEATING_TYPE));

        defs.setSchema("SomeSchema", DataSchema.emptySchema());

        assertThat(SchemaField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).getType(),
                is(SchemaField.NESTED_TYPE));

        assertThat(SchemaField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).getType(),
                is(SchemaField.NESTED_REPEATING_TYPE));
    }

    @Test
    void testMapField() {

        SchemaField schemaField = SchemaField.of(5, "Foo", Object.class);

        assertThat(schemaField.mapToFieldName("Bar").toString(),
                is("[5:Bar]=java.lang.Object"));

        assertThat(schemaField.mapToIndex(42).toString(),
                is("[42:Foo]=java.lang.Object"));

        assertThat(schemaField.mapTo(0, "Bar").toString(),
                is("[0:Bar]=java.lang.Object"));
    }
}
