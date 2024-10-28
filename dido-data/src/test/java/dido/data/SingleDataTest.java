package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SingleDataTest {

    @Test
    void stingType() {

        DidoData data1 = SingleData.of("Apple");

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(String.class)
                        .build()));

        assertThat(data1.getStringAt(1), is("Apple"));

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

        DidoData data1 = SingleData.of(true);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(boolean.class)
                        .build()));

        assertThat(data1.getBooleanAt(1), is(true));

        DidoData data2 = SingleData.named("Boolean").of(true);

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

        DidoData data1 = SingleData.of((byte) 8);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(byte.class)
                        .build()));

        assertThat(data1.getByteAt(1), is((byte) 8));

        DidoData data2 = SingleData.named("Byte").of((byte) 8);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Byte", byte.class)
                        .build()));

        assertThat(data2.getByteNamed("Byte"), is((byte) 8));

        DidoData data3 = SingleData.named("Byte").type(byte.class)
                .of((byte) 8);

        assertThat(data3, is(data2));
    }

    @Test
    void charType() {

        DidoData data1 = SingleData.of('A');

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(char.class)
                        .build()));

        assertThat(data1.getCharAt(1), is('A'));

        DidoData data2 = SingleData.named("Char").of('A');

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

        DidoData data1 = SingleData.of((short) 24);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(short.class)
                        .build()));

        assertThat(data1.getShortAt(1), is((short) 24));

        DidoData data2 = SingleData.named("Short").of((short) 24);

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

        DidoData data1 = SingleData.of(42);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(int.class)
                        .build()));

        assertThat(data1.getIntAt(1), is(42));

        DidoData data2 = SingleData.named("Quantity")
                .of(42);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Quantity", int.class)
                        .build()));

        assertThat(data2.getIntNamed("Quantity"), is( 42));

        DidoData data3 = SingleData.named("Quantity").type(int.class)
                .of(42);

        assertThat(data3, is(data2));
    }

    @Test
    void longType() {

        DidoData data1 = SingleData.of(84L);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(long.class)
                        .build()));

        assertThat(data1.getLongAt(1), is(84L));

        DidoData data2 = SingleData.named("Long")
                .of(84L);

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

        DidoData data1 = SingleData.of(42.24F);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(float.class)
                        .build()));

        assertThat(data1.getFloatAt(1), is(42.24F));

        DidoData data2 = SingleData.named("Float")
                .of(42.24F);

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

        DidoData data1 = SingleData.of(42.24);

        assertThat(data1.getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(double.class)
                        .build()));

        assertThat(data1.getDoubleAt(1), is(42.24));

        DidoData data2 = SingleData.named("Double")
                .of(42.24);

        assertThat(data2.getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Double", double.class)
                        .build()));

        assertThat(data2.getDoubleNamed("Double"), is( 42.24));

        DidoData data3 = SingleData.named("Double").type(double.class)
                .of(42.24);

        assertThat(data3, is(data2));
    }

    @Test
    void equals() {

        assertThat(SingleData.of("Apple"), is(ArrayData.of("Apple")));

        assertThat(SingleData.of(null), is(ArrayData.of(new Object[]{null})));

        assertThat(SingleData.of(42),
                is(ArrayData.valuesForSchema(
                        ArrayData.schemaBuilder().add(int.class).build()).of(42)));

        assertThat(SingleData.of(42.24),
                is(ArrayData.valuesForSchema(
                        ArrayData.schemaBuilder().add(double.class).build()).of(42.24)));

        assertThat(SingleData.of(42.24F),
                is(ArrayData.valuesForSchema(
                        ArrayData.schemaBuilder().add(float.class).build()).of(42.24F)));

        assertThat(SingleData.type(int.class).of(42),
                is(SingleData.of(42)));


    }
}