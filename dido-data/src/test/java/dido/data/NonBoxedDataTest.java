package dido.data;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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

        DidoData data2 = NonBoxedData.copy(data1);

        assertThat(data2, is(data1));
    }

    @Test
    void builder() throws ParseException {

        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2021-09-22");

        DidoData data = NonBoxedData.builder()
                .with("Fruit", "Apple")
                .withInt("Quantity", 2)
                .withDouble("Price", 26.3)
                .with("Date", date)
                .build();

        DataSchema schema = NonBoxedData.schemaBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .addNamed("Date", Date.class)
                .build();

        assertThat(data.getSchema(), is(schema));

        assertThat(data, is(ArrayData.valuesWithSchema(schema)
                .of("Apple", 2, 26.3, date)));
    }

    @Test
    void builderCopy() throws ParseException {

        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2021-09-22");

        DataSchema schema = DataSchema.builder()
                .addNamed("String", String.class)
                .addNamed("Boolean", boolean.class)
                .addNamed("Byte", byte.class)
                .addNamed("Char", char.class)
                .addNamed("Short", short.class)
                .addNamed("Int", int.class)
                .addNamed("Long", long.class)
                .addNamed("Float", float.class)
                .addNamed("Double", double.class)
                .addNamed("Date", Date.class)
                .build();

        DidoData data = NonBoxedData.builder()
                .copy(SingleData.named("String").of("Apple"))
                .copy(SingleData.named("Boolean").ofBoolean(true))
                .copy(SingleData.named("Byte").ofByte((byte) 128))
                .copy(SingleData.named("Char").ofChar('A'))
                .copy(SingleData.named("Short").ofShort((short) 4))
                .copy(SingleData.named("Int").ofInt(42))
                .copy(SingleData.named("Long").ofLong(84L))
                .copy(SingleData.named("Float").ofFloat(4.2f))
                .copy(SingleData.named("Double").ofDouble(8.4))
                .copy(SingleData.named("Date").of(date))
                .build();

        assertThat(data.getSchema(), is(schema));

        assertThat(data, is(ArrayData.valuesWithSchema(schema)
                .of("Apple", true, (byte) 128, 'A', (short) 4, 42, 84L, 4.2F, 8.4, date)));
    }

    @Test
    void builderNoSchemaCopyNoData() throws ParseException {

        DataSchema schema = DataSchema.builder()
                .addNamed("String", String.class)
                .addNamed("Boolean", boolean.class)
                .addNamed("Byte", byte.class)
                .addNamed("Char", char.class)
                .addNamed("Short", short.class)
                .addNamed("Int", int.class)
                .addNamed("Long", long.class)
                .addNamed("Float", float.class)
                .addNamed("Double", double.class)
                .addNamed("Date", Date.class)
                .build();

        DidoData original = MapData.builderForSchema(schema)
                .build();

        DidoData copy = NonBoxedData.builder()
                .copy(original)
                .build();

        assertThat(copy.getSchema(), is(original.getSchema()));

        // This won't work until we support nullable in schemas.
        assertThat(copy, not(is(original)));
    }
}