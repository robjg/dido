package dido.operators.transform;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldOperationsTest {

    static ArrayData.Schema schema = ArrayData.schemaBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.valuesForSchema(schema)
            .of("Apple", 10, 23.5);

    @Test
    void copyAt() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forWritableSchema(schema)
                .addFieldOperation(FieldOperations.copyAt(3))
                .addFieldOperation(FieldOperations.copyAt(2))
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of(10, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyNamed() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forWritableSchema(schema)
                .addFieldOperation(FieldOperations.copyNamed("Price"))
                .addFieldOperation(FieldOperations.copyNamed("Fruit"))
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of("Apple", 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copyNamedFromTo() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forWritableSchema(schema)
                .addFieldOperation(FieldOperations.copyNamed("Fruit", "Type"))
                .addFieldOperation(FieldOperations.copyNamed("Price", "Price"))
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Type", String.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of("Apple", 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void copySameName() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forWritableSchema(schema)
                .addFieldOperation(FieldOperations.copyNamed("Qty", "Qty"))
                .addFieldOperation(FieldOperations.copyNamed("Price", "Price"))
                .addFieldOperation(FieldOperations.copyNamed("Fruit", "Fruit"))
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Fruit", String.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of(10, 23.5, "Apple");

        assertThat(result, is(expectedData));
    }

    @Test
    void computeInPlace() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forWritableSchemaWithCopy(schema)
                .addFieldOperation(FieldOperations.computeNamed("Qty",
                                data -> data.getIntAt(2) * 2, int.class))
                .build();

        ArrayData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        DidoData expectedData = ArrayData.valuesForSchema(schema)
                .of("Apple", 20, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void computeNewField() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forWritableSchemaWithCopy(schema)
                .addFieldOperation(FieldOperations.computeNamed("QtyDoubled",
                        data -> data.getIntAt(2) * 2, int.class))
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .merge(schema)
                .addNamed("QtyDoubled", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of("Apple", 10, 23.5, 20);

        assertThat(result, is(expectedData));
    }

    @Test
    void remove() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forWritableSchemaWithCopy(schema)
                .addFieldOperation(FieldOperations.removeNamed("Fruit"))
                .addFieldOperation(FieldOperations.removeNamed("Price"))
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Qty", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of(10);

        assertThat(result, is(expectedData));
    }

    @Test
    void set() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forWritableSchemaWithCopy(schema)
                .addFieldOperation(FieldOperations.setNamed("Fruit", "Orange"))
                .addFieldOperation(FieldOperations.setNamed("Qty", 1234L, long.class))
                .addFieldOperation(FieldOperations.setNamed("InStock", true, boolean.class))
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", long.class)
                .addNamed("Price", double.class)
                .addNamed("InStock", boolean.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of("Orange", 1234L, 23.5, true);

        assertThat(result, is(expectedData));
    }
}