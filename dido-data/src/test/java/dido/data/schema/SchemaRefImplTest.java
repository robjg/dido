package dido.data.schema;

import dido.data.DataSchema;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class SchemaRefImplTest {

    @Test
    void equality() {

        SchemaRefImpl ref = SchemaRefImpl.named("person");

        assertThat(ref, not(is(ref)));

        DataSchema schema = DataSchema.emptySchema();

        ref.set(schema);

        assertThat(ref, is(ref));

        SchemaRefImpl ref2 = SchemaRefImpl.named("person");
        ref2.set(DataSchema.builder().add(int.class).build());

        assertThat(ref2, not(is(ref)));

        assertThat(ref2.toString(), is("Ref#person"));
    }
}