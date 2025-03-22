package dido.operators.transform;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ViewTransformBuilderTest {

    static DataSchema schema = ArrayData.schemaBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.valuesWithSchema(schema)
            .of("Apple", 10, 23.5);

    @Test
    void copyWithFieldLocBuilder() {

        DidoTransform transformation = ViewTransformBuilder.with()
                .reIndex(true)
                .forSchema(schema)
                .addOp(FieldOps.copy().index(3).with().transform()) // copies index 3 to index 3
                .addOp(FieldOps.copy().from("Qty").with().transform()) // copies Qty to index 2
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
}