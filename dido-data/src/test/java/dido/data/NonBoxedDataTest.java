package dido.data;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class NonBoxedDataTest {

    @Test
    void buildAndGet() throws ParseException {

        DataSchema schema = NonBoxedData.schemaBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .addNamed("Date", Date.class)
                .build();

        DidoData data1 = NonBoxedData.builderForSchema(schema)
                .with("Fruit", "Apple")
                .withInt("Quantity", 2)
                .withDouble("Price", 26.3)
                .with("Date", new SimpleDateFormat("yyyy-MM-dd").parse("2021-09-22"))
                .build();

        assertThat(data1.getStringNamed("Fruit"), is("Apple"));
        assertThat(data1.getIntNamed("Quantity"), is(2));
        assertThat(data1.getDoubleNamed("Price"), is(26.3));

        DataSchema schema1 = data1.getSchema();

        assertThat(schema1.getTypeNamed("Fruit"), is(String.class));
        assertThat(schema1.getTypeNamed("Quantity"), is(int.class));
        assertThat(schema1.getTypeNamed("Price"), is(double.class));
        assertThat(schema1.getTypeNamed("Date"), is(Date.class));

    }
}