package dido.data;

import dido.data.schema.SchemaBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SingleDataTest {

    @Test
    void voidType() {

        DidoData data1 = SingleData.of(null);

        assertThat(data1, is(ArrayData.of(new Object[]{null})));
        assertThat(data1.hashCode(), is(0));

        DidoData data2 = SingleData.named("Void").of(null);

        assertThat(data2, is(MapData.of("Void", null)));
        assertThat(data2.hashCode(), is(0));
    }

    @Test
    void stingType() {

        DidoData data1 = SingleData.of("Apple");

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(String.class)
                        .build()));

        assertThat(data1.getStringAt(1), is("Apple"));

        DidoData expected = ArrayData.of("Apple");
        assertThat(data1, is(expected));
        assertThat(data1.hashCode(), is(expected.hashCode()));

        DidoData data2 = SingleData.named("Fruit").of("Apple");

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Fruit", String.class)
                        .build()));

        assertThat(data2.getStringNamed("Fruit"), is("Apple"));

        assertThat(SingleData.named("Fruit").type(String.class)
                        .of("Apple")
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Fruit", String.class)
                        .build()));

        DidoData data3 = SingleData.named("Fruit").type(String.class).of("Apple");

        assertThat(data3, is(data2));
    }

    @Test
    void booleanType() {

        DidoData data1 = SingleData.ofBoolean(true);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(boolean.class)
                        .build()));

        assertThat(data1.getBooleanAt(1), is(true));

        DidoData data2 = SingleData.named("Boolean").ofBoolean(true);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Boolean", boolean.class)
                        .build()));

        assertThat(data2.getBooleanNamed("Boolean"), is(true));

        DidoData data3 = SingleData.named("Boolean").type(boolean.class)
                .of(true);

        assertThat(data3, is(data2));
    }


    @Test
    void byteType() {

        DidoData data1 = SingleData.ofByte((byte) 8);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(byte.class)
                        .build()));

        assertThat(data1.getAt(1), is((byte) 8));
        assertThat(data1.getByteAt(1), is((byte) 8));
        assertThat(data1.getShortAt(1), is((short) 8));
        assertThat(data1.getIntAt(1), is(8));
        assertThat(data1.getLongAt(1), is( 8L));
        assertThat(data1.getFloatAt(1), is( 8.0F));
        assertThat(data1.getDoubleAt(1), is( 8.0));
        assertThat(data1.getStringAt(1), is( "8"));

        FieldGetter g = ReadSchema.from(data1.getSchema()).getFieldGetterAt(1);

        assertThat(g.get(data1), is((byte) 8));
        assertThat(g.getByte(data1), is((byte) 8));
        assertThat(g.getShort(data1), is((short) 8));
        assertThat(g.getInt(data1), is(8));
        assertThat(g.getLong(data1), is( 8L));
        assertThat(g.getFloat(data1), is( 8.0F));
        assertThat(g.getDouble(data1), is( 8.0));
        assertThat(g.getString(data1), is( "8"));

        DidoData data2 = SingleData.named("Byte").ofByte((byte) 8);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Byte", byte.class)
                        .build()));

        assertThat(data2.getNamed("Byte"), is((byte) 8));
        assertThat(data2.getByteNamed("Byte"), is((byte) 8));
        assertThat(data2.getShortNamed("Byte"), is((short) 8));
        assertThat(data2.getIntNamed("Byte"), is(8));
        assertThat(data2.getLongNamed("Byte"), is( 8L));
        assertThat(data2.getFloatNamed("Byte"), is( 8.0F));
        assertThat(data2.getDoubleNamed("Byte"), is( 8.0));
        assertThat(data2.getStringNamed("Byte"), is( "8"));

        FieldGetter gf = ReadSchema.from(data2.getSchema()).getFieldGetterNamed("Byte");

        assertThat(gf.get(data2), is((byte) 8));
        assertThat(gf.getByte(data2), is((byte) 8));
        assertThat(gf.getShort(data2), is((short) 8));
        assertThat(gf.getInt(data2), is(8));
        assertThat(gf.getLong(data2), is( 8L));
        assertThat(gf.getFloat(data2), is( 8.0F));
        assertThat(gf.getDouble(data2), is( 8.0));
        assertThat(gf.getString(data2), is( "8"));

        DidoData data3 = SingleData.named("Byte").type(byte.class)
                .of((byte) 8);

        assertThat(data3, is(data2));
    }

    @Test
    void charType() {

        DidoData data1 = SingleData.ofChar('A');

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(char.class)
                        .build()));

        assertThat(data1.getCharAt(1), is('A'));

        DidoData data2 = SingleData.named("Char").ofChar('A');

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Char", char.class)
                        .build()));

        assertThat(data2.getCharNamed("Char"), is('A'));

        DidoData data3 = SingleData.named("Char").type(char.class)
                .of('A');

        assertThat(data3, is(data2));
    }

    @Test
    void shortType() {

        DidoData data1 = SingleData.ofShort((short) 24);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(short.class)
                        .build()));

        assertThat(data1.getShortAt(1), is((short) 24));

        DidoData data2 = SingleData.named("Short").ofShort((short) 24);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Short", short.class)
                        .build()));

        assertThat(data2.getShortNamed("Short"), is((short) 24));

        DidoData data3 = SingleData.named("Short").type(short.class)
                .of((short) 24);

        assertThat(data3, is(data2));
    }

    @Test
    void intType() {

        DidoData data1 = SingleData.ofInt(42);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(int.class)
                        .build()));

        assertThat(data1.getIntAt(1), is(42));

        assertThat(data1, is(SingleData.type(int.class).of(42)));

        DidoData data2 = SingleData.named("Quantity")
                .ofInt(42);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Quantity", int.class)
                        .build()));

        assertThat(data2.getIntNamed("Quantity"), is( 42));

        DidoData data3 = SingleData.named("Quantity").type(int.class)
                .of(42);

        assertThat(data3, is(data2));

        assertThat(data1,
                is(ArrayData.withSchema(
                        ArrayData.schemaBuilder().add(int.class).build()).of(42)));

    }

    @Test
    void longType() {

        DidoData data1 = SingleData.ofLong(84L);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(long.class)
                        .build()));

        assertThat(data1.getLongAt(1), is(84L));

        DidoData data2 = SingleData.named("Long")
                .ofLong(84L);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Long", long.class)
                        .build()));

        assertThat(data2.getLongNamed("Long"), is( 84L));

        DidoData data3 = SingleData.named("Long").type(long.class)
                .of(84L);

        assertThat(data3, is(data2));
    }

    @Test
    void testFloat() {

        DidoData data1 = SingleData.ofFloat(42.24F);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(float.class)
                        .build()));

        assertThat(data1.getFloatAt(1), is(42.24F));

        assertThat(data1,
                is(ArrayData.withSchema(
                        ArrayData.schemaBuilder().add(float.class).build()).of(42.24F)));

        DidoData data2 = SingleData.named("Float")
                .ofFloat(42.24F);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Float", float.class)
                        .build()));

        assertThat(data2.getFloatNamed("Float"), is( 42.24F));

        DidoData data3 = SingleData.named("Float").type(float.class)
                .of(42.24F);

        assertThat(data3, is(data2));
    }

    @Test
    void testDouble() {

        DidoData data1 = SingleData.ofDouble(42.24);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(double.class)
                        .build()));

        assertThat(data1.getDoubleAt(1), is(42.24));

        assertThat(data1, is(ArrayData.of(42.24)));
        assertThat(ArrayData.of(42.24), is(data1));

        DidoData data2 = SingleData.named("Double")
                .ofDouble(42.24);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Double", double.class)
                        .build()));

        assertThat(data2.getDoubleNamed("Double"), is( 42.24));

        DidoData data3 = SingleData.named("Double").type(double.class)
                .of(42.24);

        assertThat(data3, is(data2));

        assertThat(data3, is(SingleData.of(42.24)));
    }

}