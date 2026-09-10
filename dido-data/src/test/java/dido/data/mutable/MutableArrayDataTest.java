package dido.data.mutable;

import dido.data.*;
import dido.data.immutable.ArrayData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

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

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.50),
                DidoData.of("Orange", 2, 35.24),
                DidoData.of("Pear", 3, 26.84)));
    }

    @Test
    void newInstance() {

        MutableArrayData.ArrayDataSchema fromSchema = (MutableArrayData.ArrayDataSchema)
                MutableArrayData.schemaBuilder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Quantity", int.class)
                        .addNamed("Price", double.class)
                        .build();

        FieldSetter fruitSetter = fromSchema.getFieldSetterNamed("Fruit");
        FieldSetter qtySetter = fromSchema.getFieldSetterNamed("Quantity");
        FieldSetter priceSetter = fromSchema.getFieldSetterNamed("Price");

        MutableData data = MutableArrayData.newInstance(fromSchema);

        fruitSetter.set(data, "Pear");
        qtySetter.setInt(data, 3);
        priceSetter.setDouble(data, 26.84);

        assertThat(data, is(
                DidoData.of("Pear", 3, 26.84)));

    }

    @Test
    void updateFunction() {

        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        MutableArrayData mutableData = (MutableArrayData) MutableArrayData.withSchema(schema)
                .of("Apple", 5, 19.50);

        FromValues fromValues = ArrayData.withSchema(schema);

        BiFunction<DidoData, MutableArrayData, int[]> updateFunction =
                MutableArrayData.updateFunction(fromValues.getSchema(), mutableData.getSchema());

        int[] update1 = updateFunction.apply(fromValues.of("Apple", 6, 20.50),
                mutableData);

        assertThat(update1, is(new int[] { 2, 3}));

        assertThat(mutableData, is(DidoData.of("Apple", 6, 20.50)));

        int[] update2 = updateFunction.apply(fromValues.of("Apple", 6, 20.50),
                mutableData);

        assertThat(update2, is(new int[0]));

        assertThat(mutableData, is(DidoData.of("Apple", 6, 20.50)));

        int[] update3 = updateFunction.apply(fromValues.of("Orange", 7, 56.3),
                mutableData);

        assertThat(update3, is(new int[] {1, 2, 3}));

        assertThat(mutableData, is(DidoData.of("Orange", 7, 56.3)));

    }

    @Test
    void updateFunctionDisparateIndex() {

        DataSchema schema = DataSchema.builder()
                .addNamedAt(3, "Fruit", String.class)
                .addNamedAt(8, "Quantity", int.class)
                .addNamedAt(12, "Price", double.class)
                .build();

        MutableArrayData mutableData = (MutableArrayData) MutableArrayData.withSchema(schema)
                .of("Apple", 5, 19.50);

        FromValues fromValues = ArrayData.withSchema(schema);

        BiFunction<DidoData, MutableArrayData, int[]> updateFunction =
                MutableArrayData.updateFunction(fromValues.getSchema(), mutableData.getSchema());

        int[] update1 = updateFunction.apply(fromValues.of("Apple", 6, 20.50),
                mutableData);

        assertThat(update1, is(new int[] { 8, 12}));

        assertThat(mutableData, is(DidoData.of("Apple", 6, 20.50)));

        int[] update2 = updateFunction.apply(fromValues.of("Apple", 6, 20.50),
                mutableData);

        assertThat(update2, is(new int[0]));

        assertThat(mutableData, is(DidoData.of("Apple", 6, 20.50)));

        int[] update3 = updateFunction.apply(fromValues.of("Orange", 7, 56.3),
                mutableData);

        assertThat(update3, is(new int[] {3, 8, 12}));

        assertThat(mutableData, is(DidoData.of("Orange", 7, 56.3)));

    }

}