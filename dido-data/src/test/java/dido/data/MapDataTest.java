package dido.data;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

class MapDataTest {

    @Test
    void testBuilderNoSchema() throws ParseException {

        NamedDataBuilder builder = MapData.newBuilderNoSchema();

        NamedData data1 = builder
                .withString("type", "apple")
                .withInt("qty", 2)
                .withDouble("price", 26.3)
                .with("date", new SimpleDateFormat("yyyy-MM-dd").parse("2021-09-22"))
                .build();

        assertThat(data1.getString("type"), is("apple"));
        assertThat(data1.getInt("qty"), is(2));
        assertThat(data1.getDouble("price"), is(26.3));

        DataSchema schema1 = data1.getSchema();

        assertThat(schema1.getTypeNamed("type"), is(String.class));
        assertThat(schema1.getTypeNamed("qty"), is(int.class));
        assertThat(schema1.getTypeNamed("price"), is(double.class));
        assertThat(schema1.getTypeNamed("date"), is(Date.class));

        NamedData data2 = builder
                .withString("type", "apple")
                .withLong("qty", 2)
                .withFloat("price", 26.3F)
                .withBoolean("offer", true)
                .build();

        assertThat(data2.getString("type"), is("apple"));
        assertThat(data2.getLong("qty"), is(2L));
        assertThat((double) data2.getFloat("price"), closeTo(26.3, 0.01));
        assertThat(data2.getBoolean("offer"), is(true));

        DataSchema schema2 = data2.getSchema();

        assertThat(schema2.getTypeNamed("type"), is(String.class));
        assertThat(schema2.getTypeNamed("qty"), is(long.class));
        assertThat(schema2.getTypeNamed("price"), is(float.class));
        assertThat(schema2.getTypeNamed("offer"), is(boolean.class));
    }

    @Test
    void testCreateWithOf() {

        NamedData data = MapData.of(
                "type", "apple",
                "qty", 2,
                "price", 26.3F,
                "offer", true);

        assertThat(data.getString("type"), is("apple"));
        assertThat(data.getInt("qty"), is(2));
        assertThat((double) data.getFloat("price"), closeTo(26.3, 0.01));
        assertThat(data.getBoolean("offer"), is(true));

        DataSchema schema = data.getSchema();

        assertThat(schema.getTypeNamed("type"), is(String.class));
        assertThat(schema.getTypeNamed("qty"), is(Integer.class));
        assertThat(schema.getTypeNamed("price"), is(Float.class));
        assertThat(schema.getTypeNamed("offer"), is(Boolean.class));
    }

    @Test
    void testToString() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("type", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DidoData data1 = MapData.newBuilder(schema)
                .withString("type", "apple")
                .withInt("qty", 2)
                .withDouble("price", 26.3)
                .build();

        assertThat(data1.toString(), is("{[type]=apple, [qty]=2, [price]=26.3}"));
    }

    @Test
    void testEqualsAndHashCode() {

        DidoData data1 = MapData.newBuilderNoSchema()
                .withString("type", "apple")
                .withInt("qty", 2)
                .withDouble("price", 26.3)
                .build();

        DidoData data2 = MapData.newBuilderNoSchema()
                .withString("type", "apple")
                .withInt("qty", 2)
                .withDouble("price", 26.3)
                .build();

        assertThat(data1, is(data2));
        assertThat(data1.hashCode(), is(data2.hashCode()));
    }

    @Test
    void testBuilderOf() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("type", String.class)
                .addNamed("foo", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DidoData data1 = ArrayData.valuesFor(schema)
                .of("Apple", null, 15, 26.5);

        DidoData data2 = MapData.valuesFor(schema)
                .of("Apple", null, 15, 26.5);

        assertThat(data1, is(data2));
    }
}