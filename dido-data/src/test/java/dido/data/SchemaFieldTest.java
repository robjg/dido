package dido.data;

import dido.data.schema.SchemaBuilder;
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

        SchemaReference nestedRef = SchemaReference.named("SomeSchema");

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

        assertThat(SchemaField.of(5, "Foo", Object.class).hashCode(),
                is(SchemaField.of(5, "Foo", Object.class).hashCode()));

        DataSchema nested = SchemaBuilder.newInstance().
                addNamed("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, "Foo", nested).hashCode(),
                is(SchemaField.ofNested(5, "Foo", nested).hashCode()));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested).hashCode(),
                is(SchemaField.ofRepeating(5, "Foo", nested).hashCode()));

        SchemaReference nestedRef = SchemaReference.named("SomeSchema");

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

        assertThat(SchemaField.of(5, "Foo", Object.class),
                is(SchemaField.of(5, "Foo", Object.class)));

        DataSchema nested = SchemaBuilder.newInstance().
                addNamed("Nested", int.class)
                .build();

        assertThat(SchemaField.ofNested(5, "Foo", nested),
                is(SchemaField.ofNested(5, "Foo", nested)));

        assertThat(SchemaField.ofRepeating(5, "Foo", nested),
                is(SchemaField.ofRepeating(5, "Foo", nested)));

        SchemaReference nestedRef = SchemaReference.named("SomeSchema");

        nestedRef.set(nested);

        assertThat(SchemaField.ofNested(5, "Foo", nestedRef),
                is(SchemaField.ofNested(5, "Foo", nestedRef)));

        assertThat(SchemaField.ofRepeating(5, "Foo", nestedRef),
                is(SchemaField.ofRepeating(5, "Foo", nestedRef)));
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

        SchemaReference nestedRef = SchemaReference.named("SomeSchema");

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

        SchemaField schemaField = SchemaField.of(5, "Foo", Object.class);

        assertThat(schemaField.mapToFieldName("Bar").toString(),
                is("[5:Bar]=java.lang.Object"));

        assertThat(schemaField.mapToIndex(42).toString(),
                is("[42:Foo]=java.lang.Object"));

        assertThat(schemaField.mapTo(0, "Bar").toString(),
                is("[0:Bar]=java.lang.Object"));
    }
}
