package dido.operators.transform;

import dido.data.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldOpsTest {

    static DataSchema schema = ArrayData.schemaBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.valuesWithSchema(schema)
            .of("Apple", 10, 23.5);

    @Test
    void copyAt() {

        DidoTransform transformation = OpTransformBuilder.with()
                .reIndex(true)
                .forSchema(schema)
                .addOp(FieldOps.copyAt(3))
                .addOp(FieldOps.copyAt(2))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of(10, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyIndexAt() {

        DidoTransform transformation = OpTransformBuilder.with()
                .reIndex(true)
                .forSchema(schema)
                .addOp(FieldOps.copyAt(3, 1))
                .addOp(FieldOps.copyAt(2, 0))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Price", double.class)
                .addNamed("Qty", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of(23.5, 10);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyNamed() {

        DidoTransform transformation = OpTransformBuilder.with()
                .reIndex(true)
                .forSchema(schema)
                .addOp(FieldOps.copyNamed("Price"))
                .addOp(FieldOps.copyNamed("Fruit"))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of("Apple", 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyNamedFromTo() {

        DidoTransform transformation = OpTransformBuilder
                .forSchema(schema)
                .addOp(FieldOps.copyNamed("Fruit", "Type"))
                .addOp(FieldOps.copyNamed("Price", "Price"))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Type", String.class)
                .addNamedAt(3, "Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of("Apple", 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copySameName() {

        DidoTransform transformation = OpTransformBuilder
                .forSchema(schema)
                .addOp(FieldOps.copyNamed("Qty", "Qty"))
                .addOp(FieldOps.copyNamed("Price", "Price"))
                .addOp(FieldOps.copyNamed("Fruit", "Fruit"))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        assertThat(result, is(data));
    }

    @Test
    void copyNamedAt() {

        DidoTransform transformation = OpTransformBuilder
                .forSchema(schema)
                .addOp(FieldOps.copyNamedAt("Qty", 0))
                .addOp(FieldOps.copyNamedAt("Price", 0))
                .addOp(FieldOps.copyNamedAt("Fruit", 0))
                .build();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Fruit", String.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData result = transformation.apply(data);

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of(10, 23.5, "Apple");

        assertThat(result, is(expectedData));
    }

    @Test
    void copyNamedAtTo() {

        DidoTransform transformation = OpTransformBuilder
                .forSchema(schema)
                .addOp(FieldOps.copyNamedAt("Qty", 5, "Quantity"))
                .addOp(FieldOps.copyNamedAt("Price", 3, "ThePrice"))
                .addOp(FieldOps.copyNamedAt("Fruit", -1, "Type" ))
                .build();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Type", String.class)
                .addNamedAt(5, "Quantity", int.class)
                .addNamedAt(3, "ThePrice", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData result = transformation.apply(data);

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of("Apple", 23.5, 10);

        assertThat(result, is(expectedData));
    }

    @Test
    void computeInPlace() {

        DidoTransform transformation = OpTransformBuilder.with()
                .copy(true)
                .forSchema(schema)
                .addOp(FieldOps.computeNamed("Qty",
                        data -> data.getIntAt(2) * 2, int.class))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        DidoData expectedData = ArrayData.valuesWithSchema(schema)
                .of("Apple", 20, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void computeNewField() {

        DidoTransform transformation = OpTransformBuilder.with()
                .copy(true)
                .forSchema(schema)
                .addOp(FieldOps.computeNamed("QtyDoubled",
                        data -> data.getIntAt(2) * 2, int.class))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .merge(schema)
                .addNamed("QtyDoubled", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of("Apple", 10, 23.5, 20);

        assertThat(result, is(expectedData));
    }

    @Test
    void remove() {

        DidoTransform transformation = OpTransformBuilder.with()
                .copy(true)
                .reIndex(true)
                .forSchema(schema)
                .addOp(FieldOps.removeNamed("Fruit"))
                .addOp(FieldOps.removeNamed("Price"))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Qty", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of(10);

        assertThat(result, is(expectedData));
    }

    @Test
    void set() {

        DidoTransform transformation = OpTransformBuilder.with()
                .copy(true)
                .forSchema(schema)
                .addOp(FieldOps.setNamed("Fruit", "Orange"))
                .addOp(FieldOps.setNamed("Qty", 1234L, long.class))
                .addOp(FieldOps.setNamed("InStock", true, boolean.class))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", long.class)
                .addNamed("Price", double.class)
                .addNamed("InStock", boolean.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of("Orange", 1234L, 23.5, true);

        assertThat(result, is(expectedData));
    }

    @Test
    void setNamed() {

        DataFactoryProvider dataFactoryProvider = DataFactoryProvider.newInstance();
        SchemaFactory schemaFactory = dataFactoryProvider.getSchemaFactory();

        OpDef.Prepare prepare = FieldOps.setNamed("Fruit", "Apple")
                .prepare(DataSchema.emptySchema(), SchemaSetter.fromSchemaFactory(schemaFactory));

        WriteSchema writeSchema = WriteSchema.from(schemaFactory.toSchema());

        BiConsumer < DidoData, WritableData > action = prepare.create(
                writeSchema);

        DataFactory dataFactory = dataFactoryProvider.factoryFor(writeSchema);

        action.accept(DidoData.of(), dataFactory.getWritableData());

        DidoData result = dataFactory.toData();

        MatcherAssert.assertThat(result, Matchers.is(DidoData.of("Apple")));
    }
}