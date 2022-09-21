package dido.json;

import dido.data.DataSchema;
import dido.data.GenericData;
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

        DataIn<String> in = StreamInJsonLines.asWrapperWithPartialSchema(null)
                .inFrom(bufferType.toInputStream());

        GenericData<String> data1 = in.get();

        assertThat(data1.getString("Fruit"), is("Apple"));
        assertThat(data1.getDouble("Qty"), is(5.0));
        assertThat(data1.getDouble("Price"), is(27.2));

        GenericData<String> data2 = in.get();

        assertThat(data2.getStringAt(1), is("Orange"));
        assertThat(data2.getDoubleAt(2), is(10.0));
        assertThat(data2.getDoubleAt(3), is(31.6));

        GenericData<String> data3 = in.get();

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

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("Qty", Integer.class)
                .addField("Price", Double.class)
                .build();

        DataIn<String> in = StreamInJsonLines.asWrapperWithPartialSchema(schema)
                .inFrom(bufferType.toInputStream());

        GenericData<String> data1 = in.get();

        GenericData<String> expected1 = MapData.of(
                "Fruit", "Apple", "Qty", 5, "Price", 27.2);

        assertThat(data1.getSchema(), is(expected1.getSchema()));
        assertThat(data1, is(expected1));

        GenericData<String> data2 = in.get();

        assertThat(data2, is(MapData.of(
                "Fruit", "Orange", "Qty", 10, "Price", 31.6)));

        GenericData<String> data3 = in.get();

        assertThat(data3, is(MapData.of(
                "Fruit", "Pear", "Qty", 7, "Price", 22.1)));

        assertThat(in.get(), nullValue());

        in.close();
    }
}