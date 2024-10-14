package dido.json;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
import dido.data.SchemaBuilder;
import dido.how.DataIn;
import org.junit.jupiter.api.Test;
import org.oddjob.io.BufferType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class StreamInJsonLinesTest {

    @Test
    void testSimpleIn() throws Exception {

        BufferType bufferType = new BufferType();
        bufferType.setLines(new String[]{
                "{\"Fruit\":\"Apple\",\"Qty\":5,\"Price\":27.2}",
                "{\"Fruit\":\"Orange\",\"Qty\":10,\"Price\":31.6}",
                "{\"Fruit\":\"Pear\",\"Qty\":7,\"Price\":22.1}"
        });
        bufferType.configured();

        DataIn<DidoData> in = StreamInJsonLines.asWrapper()
                .make()
                .inFrom(bufferType.toInputStream());

        DidoData data1 = in.get();

        assertThat(data1.getStringNamed("Fruit"), is("Apple"));
        assertThat(data1.getDoubleNamed("Qty"), is(5.0));
        assertThat(data1.getDoubleNamed("Price"), is(27.2));

        DidoData data2 = in.get();

        assertThat(data2.getStringAt(1), is("Orange"));
        assertThat(data2.getDoubleAt(2), is(10.0));
        assertThat(data2.getDoubleAt(3), is(31.6));

        DidoData data3 = in.get();

        assertThat(data3.toString(), is(MapData.of(
                "Fruit", "Pear", "Qty", 7.0, "Price", 22.1).toString()));

        assertThat(in.get(), nullValue());

        in.close();
    }

    @Test
    void testSimpleInWithSchema() throws Exception {

        BufferType bufferType = new BufferType();
        bufferType.setLines(new String[]{
                "{\"Fruit\":\"Apple\",\"Qty\":5,\"Price\":27.2}",
                "{\"Fruit\":\"Orange\",\"Qty\":10,\"Price\":31.6}",
                "{\"Fruit\":\"Pear\",\"Qty\":7,\"Price\":22.1}"
        });
        bufferType.configured();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Qty", Integer.class)
                .addNamed("Price", Double.class)
                .build();

        DataIn<DidoData> in = StreamInJsonLines.asWrapper()
                .setSchema(schema)
                .setPartial(true)
                .make()
                .inFrom(bufferType.toInputStream());

        DidoData data1 = in.get();

        DidoData expected1 = MapData.of(
                "Fruit", "Apple", "Qty", 5, "Price", 27.2);

        assertThat(data1.getSchema(), is(expected1.getSchema()));
        assertThat(data1, is(expected1));

        DidoData data2 = in.get();

        assertThat(data2, is(MapData.of(
                "Fruit", "Orange", "Qty", 10, "Price", 31.6)));

        DidoData data3 = in.get();

        assertThat(data3, is(MapData.of(
                "Fruit", "Pear", "Qty", 7, "Price", 22.1)));

        assertThat(in.get(), nullValue());

        in.close();
    }

    @Test
    void whenSimpleInWithFullSchema() throws Exception {

        BufferType bufferType = new BufferType();
        bufferType.setLines(new String[]{
                "{\"Fruit\":\"Apple\",\"Qty\":5,\"Price\":27.2}",
                "{\"Fruit\":\"Orange\",\"Qty\":10,\"Price\":NaN}",
                "{\"Fruit\":\"Pear\",\"Qty\":7,\"Price\":22.1}"
        });
        bufferType.configured();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Integer.class)
                .addNamed("Price", Double.class)
                .build();

        DataIn<DidoData> in = StreamInJsonLines.asWrapper()
                .setSchema(schema)
                .make()
                .inFrom(bufferType.toInputStream());

        DidoData data1 = in.get();

        DidoData expected1 = MapData.of(
                "Fruit", "Apple", "Qty", 5, "Price", 27.2);

        assertThat(data1.getSchema(), is(expected1.getSchema()));
        assertThat(data1, is(expected1));

        DidoData data2 = in.get();

        assertThat(data2, is(MapData.of(
                "Fruit", "Orange", "Qty", 10, "Price", Double.NaN)));

        DidoData data3 = in.get();

        assertThat(data3, is(MapData.of(
                "Fruit", "Pear", "Qty", 7, "Price", 22.1)));

        assertThat(in.get(), nullValue());

        in.close();
    }

}