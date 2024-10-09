package dido.operators;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
import dido.data.ReadSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class ConcatenatorDataTest {

    @Test
    void testConcatSchema() {

        DataSchema schema1 = MapData.schemaBuilder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DataSchema schema2 = MapData.schemaBuilder()
                .addNamed("supplier", String.class)
                .build();

        DataSchema schema3 = MapData.schemaBuilder()
                .addNamed("checked", String.class)
                .addNamed("good", boolean.class)
                .build();

        ReadSchema schema = Concatenator.fromSchemas(schema1, schema2, schema3)
                .getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(6));

        assertThat(schema.getFieldNameAt(1), is("fruit"));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeNamed("fruit"), is(String.class));
        assertThat(schema.getIndexNamed("fruit"), is(1));

        assertThat(schema.getFieldNameAt(2), is("qty"));
        assertThat(schema.getTypeAt(2), is(int.class));
        assertThat(schema.getTypeNamed("qty"), is(int.class));
        assertThat(schema.getIndexNamed("qty"), is(2));

        assertThat(schema.getFieldNameAt(4), is("supplier"));
        assertThat(schema.getTypeAt(4), is(String.class));
        assertThat(schema.getTypeNamed("supplier"), is(String.class));
        assertThat(schema.getIndexNamed("supplier"), is(4));

        assertThat(schema.getFieldNameAt(5), is("checked"));
        assertThat(schema.getTypeAt(5), is(String.class));
        assertThat(schema.getTypeNamed("checked"), is(String.class));
        assertThat(schema.getIndexNamed("checked"), is(5));

        assertThat(schema.getFieldNameAt(6), is("good"));
        assertThat(schema.getTypeAt(6), is(boolean.class));
        assertThat(schema.getTypeNamed("good"), is(boolean.class));
        assertThat(schema.getIndexNamed("good"), is(6));

        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.nextIndex(4), is(5));
        assertThat(schema.nextIndex(5), is(6));
        assertThat(schema.nextIndex(6), is(0));

        assertThat(schema.getFieldNames(), contains("fruit", "qty", "price", "supplier", "checked", "good"));

        assertThat(schema.toString(), is("{[1:fruit]=java.lang.String, [2:qty]=int, [3:price]=double, [4:supplier]=java.lang.String, [5:checked]=java.lang.String, [6:good]=boolean}"));
    }

    @Test
    void testConcatData() {

        MapData.BuilderNoSchema builder = MapData.newBuilderNoSchema();

        DidoData data1 = builder
                .withString("type", "apple")
                .withInt("qty", 2)
                .withDouble("price", 26.3)
                .build();

        DidoData data2 = builder
                .withString("supplier", "Alice")
                .build();

        DidoData data3 = builder
                .withString("checked", "Bob")
                .withBoolean("good", true)
                .build();

        DidoData result = Concatenator.of(data1, data2, data3);

        assertThat(result.getStringNamed("type"), is("apple"));
        assertThat(result.getStringAt(1), is("apple"));
        assertThat(result.getIntNamed("qty"), is(2));
        assertThat(result.getIntAt(2), is(2));
        assertThat(result.getDoubleNamed("price"), is(26.3));
        assertThat(result.getDoubleAt(3), is(26.3));
        assertThat(result.getStringNamed("supplier"), is("Alice"));
        assertThat(result.getStringAt(4), is("Alice"));
        assertThat(result.getStringNamed("checked"), is("Bob"));
        assertThat(result.getStringAt(5), is("Bob"));
        assertThat(result.getBooleanNamed("good"), is(true));
        assertThat(result.getBooleanAt(6), is(true));

        assertThat(result.toString(), is("{[1:type]=apple, [2:qty]=2, [3:price]=26.3, [4:supplier]=Alice, [5:checked]=Bob, [6:good]=true}"));
    }

    @Test
    void testOtherTypes() {

        MapData.BuilderNoSchema builder = MapData.newBuilderNoSchema();

        builder.withString("first", "Ignored" );
        DidoData data1 = builder.build();

        builder.with("object", List.of("Foo") );
        DidoData data2 = builder.build();

        builder.withBoolean("boolean", true );
        DidoData data3 = builder.build();

        builder.withByte("byte", (byte) 32 );
        DidoData data4 = builder.build();

        builder.withChar("char", 'A' );
        DidoData data5 = builder.build();

        builder.withShort("short", (short) 42 );
        DidoData data6 = builder.build();

        builder.withLong("long", 42L );
        DidoData data7 = builder.build();

        builder.withFloat("float", 42.42F );
        DidoData data8 = builder.build();

        DidoData result1 = Concatenator.of(data1, data2, data3, data4, data5, data6, data7, data8);

        assertThat(result1.getNamed("object"), is(List.of("Foo")));
        assertThat(result1.getAt(2), is(List.of("Foo")));
        assertThat(result1.getNamed("object"), is(List.of("Foo")));
        assertThat(result1.getAt(2), is(List.of("Foo")));
        assertThat(result1.getBooleanNamed("boolean"), is(true));
        assertThat(result1.getBooleanAt(3), is(true));
        assertThat(result1.getByteNamed("byte"), is((byte) 32));
        assertThat(result1.getByteAt(4), is((byte) 32));
        assertThat(result1.getCharNamed("char"), is('A'));
        assertThat(result1.getCharAt(5), is('A'));
        assertThat(result1.getShortNamed("short"), is((short) 42));
        assertThat(result1.getShortAt(6), is((short) 42));
        assertThat(result1.getLongNamed("long"), is(42L));
        assertThat(result1.getLongAt(7), is(42L));
        assertThat(result1.getFloatNamed("float"), is(42.42F));
        assertThat(result1.getFloatAt(8), is(42.42F));

        DidoData result2 = Concatenator.of(data1, data2, data3, data4, data5, data6, data7, data8);

        assertThat(result1, is(result2));
        assertThat(result1.hashCode(), is(result2.hashCode()));
    }

    @Test
    void testConcatWithSameFieldNames() {

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Concatenator.of(
                        MapData.of("Fruit", "Apple"),
                        MapData.of("Fruit", "Pear")));

        assertThat(
                Concatenator.withSettings().skipDuplicates(true)
                        .of(MapData.of("Fruit", "Apple"), MapData.of("Fruit", "Pear")),
                is(MapData.of("Fruit", "Apple")));

    }

    @Test
    void fieldsCanBeExcluded() {

        DidoData data1 = MapData.of(
                "Type","Apples",
                "Variety", "Cox",
                "Quantity", 12,
                "FarmId", 2);

        DidoData data2 = MapData.of(
                "Id", "2",
                "Country", "UK",
                "Farmer", "Giles");

        DidoData expected = MapData.of(
                "Type","Apples",
                "Quantity", 12,
                "FarmId", 2,
                "Country", "UK",
                "Farmer", "Giles");

        assertThat(
                Concatenator.withSettings()
                        .excludeFields("Variety", "Id")
                        .of(data1, data2),
                is(expected));

    }


}