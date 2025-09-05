package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class SchemaReferenceTest {

    @Test
    void equality() {

        SchemaReference ref = SchemaReference.named("person");

        assertThat(ref, not(is(ref)));

        DataSchema schema = DataSchema.emptySchema();

        ref.set(schema);

        assertThat(ref, is(ref));

        SchemaReference ref2 = SchemaReference.named("person");
        ref2.set(DataSchema.builder().add(int.class).build());

        assertThat(ref2, not(is(ref)));

        assertThat(ref2.toString(), is("SchemaReference{'person'}"));
    }
}