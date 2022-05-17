package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class SchemaFieldTest {

    @Test
    void testToString() {

        assertThat(SchemaField.of(5, Object.class).toString(),
                is("[5]=java.lang.Object"));

        assertThat(SchemaField.of(5, "Foo", Object.class).toString(),
                is("[5:Foo]=java.lang.Object"));

        DataSchema<String> nested = SchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, nested).toString(),
                is("[5]={[1:Nested]=int}"));

        assertThat(SchemaField.ofNested(5, "Foo", nested).toString(),
                is("[5:Foo]={[1:Nested]=int}"));

        assertThat(SchemaField.ofRepeating(5, nested).toString(),
                is("[5]=[{[1:Nested]=int}]"));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested).toString(),
                is("[5:Foo]=[{[1:Nested]=int}]"));

        SchemaReference<String> nestedRef = SchemaReference.named("SomeSchema");

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=SchemaReference{'SomeSchema'} (unset)"));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=[SchemaReference{'SomeSchema'} (unset)]"));

        nestedRef.set(nested);

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=SchemaReference{'SomeSchema'}"));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef).toString(),
                is("[5:Foo]=[SchemaReference{'SomeSchema'}]"));

    }

    @Test
    void testHashCode() {

        assertThat(SchemaField.of(5, Object.class).hashCode(),
                is(SchemaField.of(5, Object.class).hashCode()));

        assertThat(SchemaField.of(5, "Foo", Object.class).hashCode(),
                is(SchemaField.of(5, "Foo", Object.class).hashCode()));

        DataSchema<String> nested = SchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, nested).hashCode(),
                is(SchemaField.ofNested(5, nested).hashCode()));

        assertThat(SchemaField.ofNested(5, "Foo", nested).hashCode(),
                is(SchemaField.ofNested(5, "Foo", nested).hashCode()));

        assertThat(SchemaField.ofRepeating(5, nested),
                is(SchemaField.ofRepeating(5, nested)));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested).hashCode(),
                is(SchemaField.ofRepeating(5, "Foo", nested).hashCode()));

        SchemaReference<String> nestedRef = SchemaReference.named("SomeSchema");

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef).hashCode(),
                is(SchemaField.ofNested(5, "Foo", nestedRef).hashCode()));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef).hashCode(),
                is(SchemaField.ofRepeating(5, "Foo", nestedRef).hashCode()));

        nestedRef.set(nested);

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef).hashCode(),
                is(SchemaField.ofNested(5, "Foo", nestedRef).hashCode()));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef).hashCode(),
                is(SchemaField.ofRepeating(5, "Foo", nestedRef).hashCode()));
    }

    @Test
    void testEquals() {

        assertThat(SchemaField.of(5, Object.class),
                is(SchemaField.of(5, Object.class)));

        assertThat(SchemaField.of(5, "Foo", Object.class),
                is(SchemaField.of(5, "Foo", Object.class)));

        DataSchema<String> nested = SchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, nested),
                is(SchemaField.ofNested(5, nested)));

        assertThat(SchemaField.ofNested(5, "Foo", nested),
                is(SchemaField.ofNested(5, "Foo", nested)));

        assertThat(SchemaField.ofRepeating(5, nested),
                is(SchemaField.ofRepeating(5, nested)));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested),
                is(SchemaField.ofRepeating(5, "Foo", nested)));

        SchemaReference<String> nestedRef = SchemaReference.named("SomeSchema");

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef),
                is(not(SchemaField.ofNested(5, "Foo", nestedRef))));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef),
                is(not(SchemaField.ofRepeating(5, "Foo", nestedRef))));

        nestedRef.set(nested);

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef),
                is(SchemaField.ofNested(5, "Foo", nestedRef)));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef),
                is(SchemaField.ofRepeating(5, "Foo", nestedRef)));
    }

    @Test
    void testTypes() {

        assertThat(SchemaField.of(5, Object.class).getType(),
                is(Object.class));

        assertThat(SchemaField.of(5, "Foo", Object.class).getType(),
                is(Object.class));

        DataSchema<String> nested = SchemaBuilder.forStringFields().
                addField("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, nested).getType(),
                is(SchemaField.NESTED_TYPE));

        assertThat(SchemaField.ofNested(5, "Foo", nested).getType(),
                is(SchemaField.NESTED_TYPE));

        assertThat(SchemaField.ofRepeating(5, nested).getType(),
                is(SchemaField.NESTED_REPEATING_TYPE));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested).getType(),
                is(SchemaField.NESTED_REPEATING_TYPE));

        SchemaReference<String> nestedRef = SchemaReference.named("SomeSchema");

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef).getType(),
                is(SchemaField.NESTED_TYPE));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef).getType(),
                is(SchemaField.NESTED_REPEATING_TYPE));

        nestedRef.set(nested);

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef).getType(),
                is(SchemaField.NESTED_TYPE));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef).getType(),
                is(SchemaField.NESTED_REPEATING_TYPE));
    }

    @Test
    void testMapField() {

        SchemaField<String> schemaField = SchemaField.of(5, "Foo", Object.class);

        assertThat(schemaField.mapToField(42).toString(),
                is("[5:42]=java.lang.Object"));

        assertThat(schemaField.mapToIndex(42).toString(),
                is("[42:Foo]=java.lang.Object"));
    }
}
