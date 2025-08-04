package dido.data.mutable;

import dido.data.DataSchema;
import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MutableArrayDataTest {

    @Test
    void copyAndSet() {

        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Colour", String.class)
                .build();

        DidoData data = DidoData.withSchema(schema).of("Apple", 5, 22.4);

        MutableArrayData test = MutableArrayData.copy(data);

        test.setNamed("Colour", "Red");

        assertThat(test, is(DidoData.withSchema(schema).of("Apple", 5, 22.4, "Red")));

        test.setAt(2, 10);

        assertThat(test, is(DidoData.withSchema(schema).of("Apple", 10, 22.4, "Red")));
    }

}