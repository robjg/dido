package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.data.schema.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ViewTransformBuilderTest {

    static DataSchema schema = ArrayData.schemaBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.withSchema(schema)
            .of("Apple", 10, 23.5);

    @Test
    void copyWithFieldLocBuilder() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .reIndex(true)
                .forSchema(schema)
                .addFieldView(FieldViews.copy().index(3).with().view()) // copies index 3 to index 3
                .addFieldView(FieldViews.copy().from("Qty").with().view()) // copies Qty to index 2
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of(10, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyAt() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .reIndex(true)
                .forSchema(schema)
                .addFieldView(FieldViews.copyAt(3))
                .addFieldView(FieldViews.copyAt(2))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of(10, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyIndexAt() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .reIndex(true)
                .forSchema(schema)
                .addFieldView(FieldViews.copyAt(3, 1))
                .addFieldView(FieldViews.copyAt(2, 0))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Price", double.class)
                .addNamed("Qty", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of(23.5, 10);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyNamed() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .reIndex(true)
                .forSchema(schema)
                .addFieldView(FieldViews.copyNamed("Price"))
                .addFieldView(FieldViews.copyNamed("Fruit"))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Apple", 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyNamedFromTo() {

        DidoTransform transformation = ViewTransformBuilder
                .forSchema(schema)
                .addFieldView(FieldViews.copyNamed("Fruit", "Type"))
                .addFieldView(FieldViews.copyNamed("Price", "Price"))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Type", String.class)
                .addNamedAt(3, "Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Apple", 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copySameName() {

        DidoTransform transformation = ViewTransformBuilder
                .forSchema(schema)
                .addFieldView(FieldViews.copyNamed("Qty", "Qty"))
                .addFieldView(FieldViews.copyNamed("Price", "Price"))
                .addFieldView(FieldViews.copyNamed("Fruit", "Fruit"))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        assertThat(result, is(data));
    }

    @Test
    void copyNamedAt() {

        DidoTransform transformation = ViewTransformBuilder
                .forSchema(schema)
                .addFieldView(FieldViews.copyNamedAt("Qty", 0))
                .addFieldView(FieldViews.copyNamedAt("Price", 0))
                .addFieldView(FieldViews.copyNamedAt("Fruit", 0))
                .build();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Fruit", String.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData result = transformation.apply(data);

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of(10, 23.5, "Apple");

        assertThat(result, is(expectedData));
    }

    @Test
    void copyNamedAtTo() {

        DidoTransform transformation = ViewTransformBuilder
                .forSchema(schema)
                .addFieldView(FieldViews.copyNamedAt("Qty", 5, "Quantity"))
                .addFieldView(FieldViews.copyNamedAt("Price", 3, "ThePrice"))
                .addFieldView(FieldViews.copyNamedAt("Fruit", -1, "Type"))
                .build();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Type", String.class)
                .addNamedAt(5, "Quantity", int.class)
                .addNamedAt(3, "ThePrice", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData result = transformation.apply(data);

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Apple", 23.5, 10);

        assertThat(result, is(expectedData));
    }

    @Test
    void rename() {

        DidoTransform transformation = ViewTransformBuilder
                .with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.renameAt("Qty", 5, "Quantity"))
                .addFieldView(FieldViews.renameAt("Price", 3, "ThePrice"))
                .addFieldView(FieldViews.rename("Fruit", "Type"))
                .build();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Type", String.class)
                .addNamedAt(5, "Quantity", int.class)
                .addNamedAt(3, "ThePrice", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData result = transformation.apply(data);

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Apple", 23.5, 10);

        assertThat(result, is(expectedData));
    }

    @Test
    void setNamedWithCopy() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.setNamed("Fruit", "Orange"))
                .addFieldView(FieldViews.setNamed("Qty", 1234L, long.class))
                .addFieldView(FieldViews.setNamed("InStock", true, boolean.class))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", long.class)
                .addNamed("Price", double.class)
                .addNamed("InStock", boolean.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Orange", 1234L, 23.5, true);

        assertThat(result, is(expectedData));
    }

    @Test
    void setAtWithCopy() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.setAt(1, "Orange"))
                .addFieldView(FieldViews.setAt(2, 1234L, long.class))
                .addFieldView(FieldViews.setNamedAt(4, "InStock", true))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", long.class)
                .addNamed("Price", double.class)
                .addNamed("InStock", java.lang.Boolean.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Orange", 1234L, 23.5, true);

        assertThat(result, is(expectedData));
    }

    @Test
    void removeNamed() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .reIndex(true)
                .forSchema(schema)
                .addFieldView(FieldViews.removeNamed("Fruit"))
                .addFieldView(FieldViews.removeNamed("Price"))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Qty", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of(10);

        assertThat(result, is(expectedData));
    }

    @Test
    void removeAt() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.removeAt(1))
                .addFieldView(FieldViews.removeAt(3))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamedAt(2, "Qty", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of(10);

        assertThat(result, is(expectedData));
    }

    @Test
    void mapAt() {

        UnaryOperator<String> fruitOp = String::toUpperCase;
        Function<Integer, Double> qtyOp = qty -> (double) qty * 2.5;
        Function<Double, String> priceOp = price -> "£" + price;

        DidoTransform transformation = ViewTransformBuilder.with()
                .forSchema(schema)
                .addFieldView(FieldViews.map()
                        .index(1)
                        .with().func(fruitOp))
                .addFieldView(FieldViews.map()
                        .index(2)
                        .at(3)
                        .with().type(double.class)
                        .func(qtyOp))
                .addFieldView(FieldViews.map()
                        .index(3)
                        .atLastIndex()
                        .to("DisplayPrice")
                        .with().type(String.class)
                        .func(priceOp))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamedAt(3, "Qty", double.class)
                .addNamed("DisplayPrice", String.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(schema)
                .of("APPLE", 25.0, "£23.5");

        assertThat(result, is(expectedData));
    }

    @Test
    void mapNamed() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.map()
                        .from("Qty")
                        .with().func(qty -> (int) qty * 2))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        DidoData expectedData = ArrayData.withSchema(schema)
                .of("Apple", 20, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void unaryMapNewField() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.map()
                        .from("Qty")
                        .to("Extra")
                        .with()
                        .func(qty -> (int) qty * 2))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .merge(schema)
                .addNamed("Extra", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Apple", 10, 23.5, 20);

        assertThat(result, is(expectedData));
    }

    @Test
    void unaryMapSameIndexNewName() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.map()
                        .from("Qty")
                        .to("Extra")
                        .atSameIndex()
                        .with()
                        .func(qty -> (int) qty * 2))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Extra", int.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Apple", 20, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void mapIntToIntNamed() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.map()
                        .from("Qty")
                        .with().intOp(qty -> qty * 2))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        DidoData expectedData = ArrayData.withSchema(schema)
                .of("Apple", 20, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void mapIntToIntAt() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.map()
                        .index(2)
                        .with().intOp(qty -> qty * 2))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        DidoData expectedData = ArrayData.withSchema(schema)
                .of("Apple", 20, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void mapLongToLongNamedAt() {

        DataSchema schema = ArrayData.schemaBuilder()
                .addNamed("BigNumber", long.class)
                .build();

        DidoData data = DidoData.withSchema(schema)
                .of(1000L);

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.map()
                        .from("BigNumber")
                        .at(20)
                        .with().longOp(qty -> qty * 2))
                .addFieldView(FieldViews.map()
                        .from("BigNumber")
                        .to("AnotherBigNumber").at(15)
                        .with().longOp(qty -> qty * 2))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = ArrayData.schemaBuilder()
                .addNamedAt(15, "AnotherBigNumber", long.class)
                .addNamedAt(20, "BigNumber", long.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of(2000L, 2000L);

        assertThat(result, is(expectedData));
    }

    @Test
    void mapDoubleToDoubleAt() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .existingFields(true)
                .forSchema(schema)
                .addFieldView(FieldViews.map()
                        .index(3)
                        .with().doubleOp(price -> price * 2))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        DidoData expectedData = ArrayData.withSchema(schema)
                .of("Apple", 10, 47.0);

        assertThat(result, is(expectedData));
    }
}