package dido.data.generic;

import dido.data.SchemaReference;
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

        SchemaReference nestedRef = SchemaReference.named("SomeSchema");

        assertThat(genericField.ofNested(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=SchemaReference{'SomeSchema'} (unset)"));

        assertThat(genericField.ofRepeating(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=[SchemaReference{'SomeSchema'} (unset)]"));

        nestedRef.set(nested);

        assertThat(genericField.ofNested(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=SchemaReference{'SomeSchema'}"));

        assertThat(genericField.ofRepeating(5, "Foo", nestedRef).toString(),
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

        SchemaReference nestedRef = SchemaReference.named("SomeSchema");

        assertThat(genericField.ofNested(5, "Foo", nestedRef).hashCode(),
                is(genericField.ofNested(5, "Foo", nestedRef).hashCode()));

        assertThat(genericField.ofRepeating(5, "Foo", nestedRef).hashCode(),
                is(genericField.ofRepeating(5, "Foo", nestedRef).hashCode()));

        nestedRef.set(nested);

        assertThat(genericField.ofNested(5, "Foo", nestedRef).hashCode(),
                is(genericField.ofNested(5, "Foo", nestedRef).hashCode()));

        assertThat(genericField.ofRepeating(5, "Foo", nestedRef).hashCode(),
                is(genericField.ofRepeating(5, "Foo", nestedRef).hashCode()));
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

        SchemaReference nestedRef = SchemaReference.named("SomeSchema");

        nestedRef.set(nested);

        assertThat(genericField.ofNested(5, "Foo", nestedRef),
                is(genericField.ofNested(5, "Foo", nestedRef)));

        assertThat(genericField.ofRepeating(5, "Foo", nestedRef),
                is(genericField.ofRepeating(5, "Foo", nestedRef)));
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

        SchemaReference nestedRef = SchemaReference.named("SomeSchema");

        assertThat(genericField.ofNested(5, "Foo", nestedRef).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(genericField.ofRepeating(5, "Foo", nestedRef).getType(),
                is(GenericSchemaField.NESTED_REPEATING_TYPE));

        nestedRef.set(nested);

        assertThat(genericField.ofNested(5, "Foo", nestedRef).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(genericField.ofRepeating(5, "Foo", nestedRef).getType(),
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
