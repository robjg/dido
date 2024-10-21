package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SingleDataTest {

    @Test
    void schema() {

        assertThat(SingleData.type(String.class)
                        .of("Apple")
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(String.class)
                        .build()));

        assertThat(SingleData.intType()
                        .of(42)
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(int.class)
                        .build()));

        assertThat(SingleData.longType()
                        .of(42L)
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(long.class)
                        .build()));

        assertThat(SingleData.doubleType()
                        .of(42.24)
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(double.class)
                        .build()));

        assertThat(SingleData.floatType()
                        .of(42.24F)
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .add(float.class)
                        .build()));

        assertThat(SingleData.named("Fruit").type(String.class)
                        .of("Apple")
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Fruit", String.class)
                        .build()));

        assertThat(SingleData.named("Quantity").intType()
                        .of(42)
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Quantity", int.class)
                        .build()));

        assertThat(SingleData.named("Timestamp").longType()
                        .of(42L)
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Timestamp", long.class)
                        .build()));

        assertThat(SingleData.named("Price").doubleType()
                        .of(42.24)
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Price", double.class)
                        .build()));

        assertThat(SingleData.named("Tax").floatType()
                        .of(42.24F)
                        .getSchema(),
                is(SchemaBuilder.newInstance()
                        .addNamed("Tax", float.class)
                        .build()));

    }

    @Test
    void equals() {

        assertThat(SingleData.of("Apple"), is(ArrayData.of("Apple")));

        assertThat(SingleData.of(null), is(ArrayData.of(new Object[] { null })));

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