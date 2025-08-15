package dido.data.util;

import dido.data.DataSchema;
import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EmptyDataTest {

    @Test
    void doesNotHaveData() {

        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        DidoData data = EmptyData.withSchema(schema);

        assertThat(data.hasAt(1), is(false));
        assertThat(data.hasAt(2), is(false));
        assertThat(data.hasAt(3), is(false));
        assertThat(data.hasAt(4), is(false));

        assertThat(data.hasNamed("Fruit"), is(false));
        assertThat(data.hasNamed("Qty"), is(false));
        assertThat(data.hasNamed("Price"), is(false));

        assertThat(data.toString(), is("{[1:Fruit]=null, [2:Qty]=null, [3:Price]=null}"));

    }
}