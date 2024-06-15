package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GenericSchemaFieldTest {

    @Test
    void testToString() {

        assertThat(GenericSchemaField.of(5, Object.class).toString(),
                is("[5]=java.lang.Object"));

        assertThat(GenericSchemaField.of(5, "Foo", Object.class).toString(),
                is("[5:Foo]=java.lang.Object"));

        GenericDataSchema<String> nested = SchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(GenericSchemaField.ofNested(5, nested).toString(),
                is("[5]={[1:Nested]=int}"));

        assertThat(GenericSchemaField.ofNested(5, "Foo", nested).toString(),
                is("[5:Foo]={[1:Nested]=int}"));

        assertThat(GenericSchemaField.ofRepeating(5, nested).toString(),
                is("[5]=[{[1:Nested]=int}]"));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nested).toString(),
                is("[5:Foo]=[{[1:Nested]=int}]"));

        SchemaReference<String> nestedRef = SchemaReference.named("SomeSchema");

        assertThat(GenericSchemaField.ofNested(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=SchemaReference{'SomeSchema'} (unset)"));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=[SchemaReference{'SomeSchema'} (unset)]"));

        nestedRef.set(nested);

        assertThat(GenericSchemaField.ofNested(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=SchemaReference{'SomeSchema'}"));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=[SchemaReference{'SomeSchema'}]"));

    }

    @Test
    void testHashCode() {

        assertThat(GenericSchemaField.of(5, Object.class).hashCode(),
                is(GenericSchemaField.of(5, Object.class).hashCode()));

        assertThat(GenericSchemaField.of(5, "Foo", Object.class).hashCode(),
                is(GenericSchemaField.of(5, "Foo", Object.class).hashCode()));

        GenericDataSchema<String> nested = SchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(GenericSchemaField.ofNested(5, nested).hashCode(),
                is(GenericSchemaField.ofNested(5, nested).hashCode()));

        assertThat(GenericSchemaField.ofNested(5, "Foo", nested).hashCode(),
                is(GenericSchemaField.ofNested(5, "Foo", nested).hashCode()));

        assertThat(GenericSchemaField.ofRepeating(5, nested),
                is(GenericSchemaField.ofRepeating(5, nested)));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nested).hashCode(),
                is(GenericSchemaField.ofRepeating(5, "Foo", nested).hashCode()));

        SchemaReference<String> nestedRef = SchemaReference.named("SomeSchema");

        assertThat(GenericSchemaField.ofNested(5, "Foo", nestedRef).hashCode(),
                is(GenericSchemaField.ofNested(5, "Foo", nestedRef).hashCode()));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nestedRef).hashCode(),
                is(GenericSchemaField.ofRepeating(5, "Foo", nestedRef).hashCode()));

        nestedRef.set(nested);

        assertThat(GenericSchemaField.ofNested(5, "Foo", nestedRef).hashCode(),
                is(GenericSchemaField.ofNested(5, "Foo", nestedRef).hashCode()));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nestedRef).hashCode(),
                is(GenericSchemaField.ofRepeating(5, "Foo", nestedRef).hashCode()));
    }

    @Test
    void testEquals() {

        assertThat(GenericSchemaField.of(5, Object.class),
                is(GenericSchemaField.of(5, Object.class)));

        assertThat(GenericSchemaField.of(5, "Foo", Object.class),
                is(GenericSchemaField.of(5, "Foo", Object.class)));

        GenericDataSchema<String> nested = SchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(GenericSchemaField.ofNested(5, nested),
                is(GenericSchemaField.ofNested(5, nested)));

        assertThat(GenericSchemaField.ofNested(5, "Foo", nested),
                is(GenericSchemaField.ofNested(5, "Foo", nested)));

        assertThat(GenericSchemaField.ofRepeating(5, nested),
                is(GenericSchemaField.ofRepeating(5, nested)));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nested),
                is(GenericSchemaField.ofRepeating(5, "Foo", nested)));

        SchemaReference<String> nestedRef = SchemaReference.named("SomeSchema");

        // Need to think if this important - are schemas equal when ref not set.

        assertThat(GenericSchemaField.ofNested(5, "Foo", nestedRef),
                is(GenericSchemaField.ofNested(5, "Foo", nestedRef)));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nestedRef),
                is(GenericSchemaField.ofRepeating(5, "Foo", nestedRef)));

        nestedRef.set(nested);

        assertThat(GenericSchemaField.ofNested(5, "Foo", nestedRef),
                is(GenericSchemaField.ofNested(5, "Foo", nestedRef)));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nestedRef),
                is(GenericSchemaField.ofRepeating(5, "Foo", nestedRef)));
    }

    @Test
    void testTypes() {

        assertThat(GenericSchemaField.of(5, Object.class).getType(),
                is(Object.class));

        assertThat(GenericSchemaField.of(5, "Foo", Object.class).getType(),
                is(Object.class));

        GenericDataSchema<String> nested = SchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(GenericSchemaField.ofNested(5, nested).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(GenericSchemaField.ofNested(5, "Foo", nested).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(GenericSchemaField.ofRepeating(5, nested).getType(),
                is(GenericSchemaField.NESTED_REPEATING_TYPE));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nested).getType(),
                is(GenericSchemaField.NESTED_REPEATING_TYPE));

        SchemaReference<String> nestedRef = SchemaReference.named("SomeSchema");

        assertThat(GenericSchemaField.ofNested(5, "Foo", nestedRef).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nestedRef).getType(),
                is(GenericSchemaField.NESTED_REPEATING_TYPE));

        nestedRef.set(nested);

        assertThat(GenericSchemaField.ofNested(5, "Foo", nestedRef).getType(),
                is(GenericSchemaField.NESTED_TYPE));

        assertThat(GenericSchemaField.ofRepeating(5, "Foo", nestedRef).getType(),
                is(GenericSchemaField.NESTED_REPEATING_TYPE));
    }

    @Test
    void testMapField() {

        GenericSchemaField<String> schemaField = GenericSchemaField.of(5, "Foo", Object.class);

        assertThat(schemaField.mapToField(42).toString(),
                is("[5:42]=java.lang.Object"));

        assertThat(schemaField.mapToIndex(42).toString(),
                is("[42:Foo]=java.lang.Object"));
    }
}
