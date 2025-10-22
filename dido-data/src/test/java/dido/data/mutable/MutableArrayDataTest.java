package dido.data.mutable;

import dido.data.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
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

    @Test
    void factoryAndSchema() {

        DataSchema schema = MutableArrayData.schemaBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Colour", String.class)
                .build();

        MutableArrayData data = (MutableArrayData) MutableArrayData.withSchema(schema)
                .of("Apple", 5, 22.4);

        FieldSetter quantitySetter = ((WriteSchema) schema).getFieldSetterNamed("Qty");

        quantitySetter.set(data, 10);

        FieldGetter quantityGetter = ((ReadSchema) schema).getFieldGetterNamed("Qty");

        assertThat(quantityGetter.get(data), is(10));

    }

    @Test
    void many() {

        DataSchema fromSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> didoData = MutableArrayData.withSchema(fromSchema)
                .many()
                .of("Apple", 5, 19.50)
                .of("Orange", 2, 35.24)
                .of("Pear", 3, 26.84)
                .toList();

        // Probably not what we want!
        assertThat(didoData, contains(
                DidoData.of("Pear", 3, 26.84),
                DidoData.of("Pear", 3, 26.84),
                DidoData.of("Pear", 3, 26.84)));
    }

}