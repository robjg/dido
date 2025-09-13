package dido.data.generic;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.data.schema.SchemaDefs;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GenericSchemaFieldTest {

    static class Grr {
    }

    static Grr FOO = new Grr() {
        @Override
        public String toString() {
            return "Foo";
        }
    };
    static Grr NESTED = new Grr() {
        @Override
        public String toString() {
            return "Nested";
        }
    };
    static Grr OTHER = new Grr() {
        @Override
        public String toString() {
            return "Other";
        }
    };

    Map<String, Grr> fieldNameMapping = Map.of(
            FOO.toString(), FOO, NESTED.toString(), NESTED, OTHER.toString(), OTHER);

    @Test
    void testToString() {

        GenericSchemaField.Of<Grr> genericField = GenericSchemaField.with(fieldNameMapping::get);

        assertThat(genericField.of(5, "Foo", Object.class).toString(),
                is("[5:Foo]=java.lang.Object"));

        GenericDataSchema<String> nested = GenericSchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(genericField.ofNested(5, "Foo", nested).toString(),
                is("[5:Foo]={[1:Nested]=int}"));

        assertThat(genericField.ofRepeating(5, "Foo", nested).toString(),
                is("[5:Foo]=[{[1:Nested]=int}]"));

        SchemaField.RefFactory ref = genericField.ofRef(5, "Foo", "SomeSchema");

        assertThat(ref.toString(),
                is("RefFactory{5:Foo]=SomeSchema}"));

        SchemaField.RefFactory repeatingRef = genericField.ofRepeatingRef(5, "Foo", "SomeSchema");

        assertThat(ref.toString(),
                is("RefFactory{5:Foo]=SomeSchema}"));

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

        GenericSchemaField.Of<Grr> genericField = GenericSchemaField.with(fieldNameMapping::get);

        assertThat(genericField.of(5, "Foo", Object.class).hashCode(),
                is(genericField.of(5, "Foo", Object.class).hashCode()));

        GenericDataSchema<String> nested = GenericSchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(genericField.ofNested(5, "Foo", nested).hashCode(),
                is(genericField.ofNested(5, "Foo", nested).hashCode()));

        assertThat(genericField.ofRepeating(5, "Foo", nested).hashCode(),
                is(genericField.ofRepeating(5, "Foo", nested).hashCode()));

        SchemaDefs defs = SchemaDefs.newInstance();

        assertThat(genericField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).hashCode(),
                is(5));

        assertThat(genericField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).hashCode(),
                is(5));
    }

    @Test
    void testEquals() {

        GenericSchemaField.Of<Grr> genericField = GenericSchemaField.with(fieldNameMapping::get);

        assertThat(genericField.of(5, "Foo", Object.class),
                is(genericField.of(5, "Foo", Object.class)));

        GenericDataSchema<Grr> nested = GenericSchemaBuilder.forFieldType(Grr.class, fieldNameMapping::get).
                addField(NESTED, int.class)
                .build();

        assertThat(genericField.ofNested(5, "Foo", nested),
                is(genericField.ofNested(5, "Foo", nested)));

        assertThat(genericField.ofRepeating(5, "Foo", nested),
                is(genericField.ofRepeating(5, "Foo", nested)));

        SchemaDefs defs = SchemaDefs.newInstance();
        defs.setSchema("SomeSchema", DataSchema.emptySchema());

        assertThat(genericField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs),
                is(genericField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs)));

        assertThat(genericField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs),
                is(genericField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs)));
    }

    @Test
    void testTypes() {

        GenericSchemaField.Of<Grr> genericField = GenericSchemaField.with(fieldNameMapping::get);

        assertThat(genericField.of(5, "Foo", Object.class).getType(),
                is(Object.class));

        GenericDataSchema<Grr> nested = GenericSchemaBuilder.forFieldType(Grr.class, fieldNameMapping::get).
                addField(NESTED, int.class)
                .build();

        assertThat(genericField.ofNested(5, "Foo", nested).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(genericField.ofRepeating(5, "Foo", nested).getType(),
                is(GenericSchemaField.NESTED_REPEATING_TYPE));

        SchemaDefs defs = SchemaDefs.newInstance();

        assertThat(genericField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(genericField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).getType(),
                is(GenericSchemaField.NESTED_REPEATING_TYPE));

        defs.setSchema("SomeSchema", DataSchema.emptySchema());

        assertThat(genericField.ofRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(genericField.ofRepeatingRef(5, "Foo", "SomeSchema")
                        .toSchemaField(defs).getType(),
                is(GenericSchemaField.NESTED_REPEATING_TYPE));
    }

    @Test
    void testMapField() {

        GenericSchemaField.Of<Grr> genericField = GenericSchemaField.with(fieldNameMapping::get);

        GenericSchemaField<Grr> schemaField = genericField.of(5, "Foo", Object.class);

        assertThat(schemaField.mapToField(OTHER).toString(),
                is("[5:Other]=java.lang.Object"));

        assertThat(schemaField.mapToIndex(42).toString(),
                is("[42:Foo]=java.lang.Object"));

        assertThat(schemaField.mapTo(0, "Bar").toString(),
                is("[0:Bar]=java.lang.Object"));
    }
}
