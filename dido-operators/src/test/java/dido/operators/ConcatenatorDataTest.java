package dido.operators;

import dido.data.*;
import dido.data.util.DataBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

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

        DataSchema expected = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .addNamed("supplier", String.class)
                .addNamed("checked", String.class)
                .addNamed("good", boolean.class)
                .build();


        assertThat(schema, is(expected));
    }

    @Test
    void testConcatData() {

        DataBuilder builder = MapData.builder();

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

        DataSchema expected = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .addNamed("supplier", String.class)
                .addNamed("checked", String.class)
                .addNamed("good", boolean.class)
                .build();

        assertThat(result, is(DidoData.withSchema(expected).of("apple", 2, 26.3, "Alice", "Bob", true)));
    }

    @Test
    void testOtherTypes() {

        DataBuilder builder = MapData.builder();

        builder.withString("first", "Ignored");
        DidoData data1 = builder.build();

        builder.with("object", List.of("Foo"));
        DidoData data2 = builder.build();

        builder.withBoolean("boolean", true);
        DidoData data3 = builder.build();

        builder.withByte("byte", (byte) 32);
        DidoData data4 = builder.build();

        builder.withChar("char", 'A');
        DidoData data5 = builder.build();

        builder.withShort("short", (short) 42);
        DidoData data6 = builder.build();

        builder.withLong("long", 42L);
        DidoData data7 = builder.build();

        builder.withFloat("float", 42.42F);
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
    void duplicates() {

        DidoData data1 = MapData.of(
                "Id", "Apple",
                "Quantity", 12,
                "FarmId", 2);

        DidoData data2 = MapData.of(
                "Id", 2,
                "Farmer", "Giles");

        DidoData expected = MapData.of(
                "Id", "Apple",
                "Quantity", 12,
                "FarmId", 2,
                "Id_", 2,
                "Farmer", "Giles");

        assertThat(
                Concatenator.of(data1, data2),
                is(expected));
    }

    @Test
    void testSkipDuplicates() {

        assertThat(
                Concatenator.with().skipDuplicates(true)
                        .of(MapData.of("Fruit", "Apple"), MapData.of("Fruit", "Pear")),
                is(MapData.of("Fruit", "Apple")));

    }

    @Test
    void fieldsCanBeExcluded() {

        DidoData data1 = MapData.of(
                "Type", "Apples",
                "Variety", "Cox",
                "Quantity", 12,
                "FarmId", 2);

        DidoData data2 = MapData.of(
                "Id", "2",
                "Country", "UK",
                "Farmer", "Giles");

        DidoData expected = MapData.of(
                "Type", "Apples",
                "Quantity", 12,
                "FarmId", 2,
                "Country", "UK",
                "Farmer", "Giles");

        assertThat(
                Concatenator.with()
                        .excludeFields("Variety", "Id")
                        .of(data1, data2),
                is(expected));

    }

    @Test
    void getterWork() {

        DidoData data1 = MapData.of(
                "Type", "Apples",
                "Variety", "Cox");

        DidoData data2 = ArrayData.builder()
                .withInt("Quantity", 5)
                .withDouble("Price", 27.2)
                .build();

        DidoData result = Concatenator.of(data1, data2);

        ReadStrategy readStrategy = ReadStrategy.fromSchema(result.getSchema());

        FieldGetter typeGetter = readStrategy.getFieldGetterNamed("Type");
        FieldGetter varietyGetter = readStrategy.getFieldGetterNamed("Variety");
        FieldGetter quantityGetter = readStrategy.getFieldGetterNamed("Quantity");
        FieldGetter priceGetter = readStrategy.getFieldGetterNamed("Price");

        assertThat(typeGetter.getString(result), is("Apples"));
        assertThat(varietyGetter.get(result), is("Cox"));
        assertThat(quantityGetter.getInt(result), is(5));
        assertThat(priceGetter.getDouble(result), is(27.2));
    }

    @Test
    void nullData() {

        DataBuilder builder = MapData.builder();

        DidoData data1 = builder
                .withString("type", "apple")
                .withInt("qty", 2)
                .withDouble("price", 26.3)
                .build();

        DidoData data2 = builder
                .withString("supplier", "Alice")
                .build();

        Concatenator concatenator = Concatenator.fromSchemas(data1.getSchema(), data2.getSchema());

        DidoData result1 = concatenator.concat(null, data2);
        assertThat(result1, is(DidoData.withSchema(concatenator.getSchema())
                .of(null, null, null, "Alice")));

        DidoData result2 = concatenator.concat(data1, null);
        assertThat(result2, is(DidoData.withSchema(concatenator.getSchema())
                .of("apple", 2, 26.3, null)));

        DidoData result3 = concatenator.concat(null, null);
        assertThat(result3, is(DidoData.withSchema(concatenator.getSchema()).of(null, null, null, null)));

        FieldGetter getter1 = concatenator.getSchema().getFieldGetterAt(1);
        FieldGetter getter2 = concatenator.getSchema().getFieldGetterAt(2);
        FieldGetter getter3 = concatenator.getSchema().getFieldGetterAt(3);
        FieldGetter getter4 = concatenator.getSchema().getFieldGetterAt(4);

        assertThat(getter1.getString(result1), nullValue());
        assertThat(getter2.has(result1), is(false));
        assertThat(getter3.getDouble(result2), is(26.3));
        assertThat(getter4.get(result3), nullValue());

    }
}