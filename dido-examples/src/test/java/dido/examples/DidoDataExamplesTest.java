package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.json.DataInJson;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DidoDataExamplesTest {

    @Test
    void accessors() {

        // #snippet6{
        DidoData data = DataInJson.with()
                .schema(DataSchema.builder()
                        .addNamed("Qty", int.class)
                        .build())
                .partialSchema(true)
                .mapFromString()
                .apply("{ \"Fruit\"=\"Apple\", \"Qty\"=5, \"Price\"=27.2 }");

        assertThat(data.getAt(1), is("Apple"));
        assertThat(data.getAt(2), is(5));
        assertThat(data.getAt(3), is(27.2));

        assertThat(data.getNamed("Fruit"), is("Apple"));
        assertThat(data.getNamed("Qty"), is(5));
        assertThat(data.getNamed("Price"), is(27.2));
        // }#snippet6

        // #snippet7{
        assertThat(data.getStringAt(1), is("Apple"));
        assertThat(data.getIntAt(2), is(5));
        assertThat(data.getDoubleAt(3), is(27.2));

        assertThat(data.getStringNamed("Fruit"), is("Apple"));
        assertThat(data.getIntNamed("Qty"), is(5));
        assertThat(data.getDoubleNamed("Price"), is(27.2));
        // }#snippet7

        // #snippet8{
        DataSchema schema = data.getSchema();

        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeAt(2), is(int.class));
        assertThat(schema.getTypeAt(3), is(double.class));

        assertThat(schema.getTypeNamed("Fruit"), is(String.class));
        assertThat(schema.getTypeNamed("Qty"), is(int.class));
        assertThat(schema.getTypeNamed("Price"), is(double.class));
        // }#snippet8
    }


    @Test
    void dataCreationNoSchema() {

        // #snippet1{
        DidoData data = DidoData.of("Apple", 5, 15.6);

        assertThat(data.toString(), is("{[1:f_1]=Apple, [2:f_2]=5, [3:f_3]=15.6}"));

        assertThat(data.getSchema().toString(), is("{[1:f_1]=java.lang.String, [2:f_2]=java.lang.Integer, [3:f_3]=java.lang.Double}"));
        // }#snippet1
    }

    @Test
    void dataCreationWithBuilder() {

        // #snippet2{
        DidoData data = DidoData.builder()
                .with("Fruit", "Apple")
                .withInt("Qty", 5)
                .withDouble("Price", 15.6)
                .build();

        assertThat(data.toString(), is("{[1:Fruit]=Apple, [2:Qty]=5, [3:Price]=15.6}"));

        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=double}"));
        // }#snippet2
    }

    @Test
    void dataCreationWithSchema() {

        // #snippet3{
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();
        // }#snippet3

        // #snippet4{
        DidoData data = DidoData.valuesWithSchema(schema)
                .of("Apple", 5, 15.6);

        assertThat(data.toString(), is("{[1:Fruit]=Apple, [2:Qty]=5, [3:Price]=15.6}"));

        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=double}"));
        // }#snippet4
    }

    @Test
    void dataCreationWithSchemaFromBuilder() {

        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        // #snippet5{
        DidoData data = DidoData.builderForSchema(schema)
                .with("Fruit", "Apple")
                .withInt("Qty", 5)
                .withDouble("Price", 15.6)
                .build();

        assertThat(data.toString(), is("{[1:Fruit]=Apple, [2:Qty]=5, [3:Price]=15.6}"));

        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=double}"));
        // }#snippet5
    }
}
