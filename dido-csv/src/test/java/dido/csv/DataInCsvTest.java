package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.data.immutable.MapData;
import dido.data.schema.SchemaBuilder;
import dido.how.DataIn;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataInCsvTest {

    @Test
    void testWithDefaults() {

        String records =
                "Apple,5,19.50" + System.lineSeparator() +
                        "Orange,2,35.24" + System.lineSeparator();

        DataSchema expectedSchema = DataSchema.builder()
                .add(String.class)
                .add(String.class)
                .add(String.class)
                .build();

        List<DidoData> expected = DidoData.withSchema(expectedSchema)
                .many()
                .of("Apple", "5", "19.50")
                .of("Orange", "2", "35.24")
                .toList();

        try (DataIn dataIn = DataInCsv.fromReader(new StringReader(records))) {

            List<DidoData> results = dataIn.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
            assertThat(results.get(0).getSchema(), is(expectedSchema));
        }
    }

    @Test
    void testWithSchema() {

        String records =
                "Apple,5,19.50" + System.lineSeparator() +
                        "Orange,2,35.24" + System.lineSeparator();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> expected = ArrayData.withSchema(schema)
                .many()
                .of("Apple", 5, 19.5)
                .of("Orange", 2, 35.24)
                .toList();

        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .fromReader(new StringReader(records))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
            assertThat(results.get(0).getSchema(), is(schema));
        }
    }

    @Test
    void testWithPartialSchema() {

        String records = "Fruit,Quantity,Price" + System.lineSeparator() +
                "Apple,5,19.50" + System.lineSeparator() +
                "Orange,2,35.24" + System.lineSeparator();

        DataSchema someSchema = SchemaBuilder.newInstance()
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> expected = ArrayData.withSchema(schema)
                .many()
                .of("Apple", 5, 19.5)
                .of("Orange", 2, 35.24)
                .toList();

        try (DataIn in = DataInCsv.with()
                .partialSchema(someSchema)
                .fromReader(new StringReader(records))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
            assertThat(results.get(0).getSchema(), is(schema));
        }
    }

    @Test
    void outOfOrderHeader() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Fruit", String.class)
                .build();

        List<DidoData> expected = ArrayData.withSchema(schema)
                .many()
                .of(5, 19.5, "Apple")
                .of(2, 35.24, "Orange")
                .of(3, 17.65, "Banana")
                .toList();

        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .header(true)
                .fromInputStream(Objects.requireNonNull(
                        getClass().getResourceAsStream("/data/FruitWithHeader.csv")))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
            assertThat(results.get(0).getSchema(), is(schema));
        }
    }

    @Test
    void outOfOrderHeaderLessColumns() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Qty", int.class)
                .addNamed("Fruit", String.class)
                .build();

        List<DidoData> expected = ArrayData.withSchema(schema)
                .many()
                .of(5, "Apple")
                .of(2, "Orange")
                .of(3, "Banana")
                .toList();

        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .header(true)
                .fromInputStream(Objects.requireNonNull(
                        getClass().getResourceAsStream("/data/FruitWithHeader.csv")))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
            assertThat(results.get(0).getSchema(), is(schema));
        }
    }

    @Test
    void outOfOrderHeaderMoreColumns() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Qty", int.class)
                .addNamed("Description", String.class)
                .addNamed("Price", double.class)
                .addNamed("Fruit", String.class)
                .build();

        List<DidoData> expected = ArrayData.withSchema(schema)
                .many()
                .of(5, null, 19.5, "Apple")
                .of(2, null, 35.24, "Orange")
                .of(3, null, 17.65, "Banana")
                .toList();

        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .header(true)
                .fromInputStream(Objects.requireNonNull(
                        getClass().getResourceAsStream("/data/FruitWithHeader.csv")))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
            assertThat(results.get(0).getSchema(), is(schema));
        }
    }

    @Test
    void testEmptyValues() {

        try (DataIn dataIn = DataInCsv.fromReader(new StringReader(",,"))) {

            Iterator<DidoData> iterator = dataIn.iterator();

            assertThat(iterator.hasNext(), is(true));

            DidoData data = iterator.next();

            DataSchema schema = data.getSchema();

            assertThat(schema.lastIndex(), is(3));

            assertThat(data.getStringAt(1), is(""));
            assertThat(data.getStringAt(2), is(""));
            assertThat(data.getStringAt(3), is(""));

            assertThat(iterator.hasNext(), is(false));
        }
    }

    @Test
    void mapFromString() {

        DidoData data = DataInCsv.mapFromString()
                .apply("Apple,5,27.2");

        DidoData expected = MapData.builder()
                .withString("f_1", "Apple")
                .withString("f_2", "5")
                .withString("f_3", "27.2")
                .build();

        assertThat(data, is(expected));
    }

    @Test
    void mapFromStringWithSchema() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Type", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        DidoData data = DataInCsv.with()
                .schema(schema)
                .mapFromString()
                .apply("Apple,5,27.2");

        DidoData expected = MapData.builder()
                .withString("Type", "Apple")
                .withInt("Quantity", 5)
                .withDouble("Price", 27.2)
                .build();

        assertThat(data, is(expected));
    }

    @Test
    void testOneAheadIterator() {

        Iterator<String> original = List.of("one", "two", "three").iterator();

        assertThat(original.hasNext(), is(true));

        String first = original.next();
        assertThat(first, is("one"));

        Iterator<String> test = new DataInCsv.OneAheadIterator<>(original, first);

        assertThat(test.hasNext(), is(true));
        assertThat(test.next(), is("one"));

        assertThat(test.hasNext(), is(true));
        assertThat(test.next(), is("two"));

        assertThat(test.hasNext(), is(true));
        assertThat(test.next(), is("three"));

        assertThat(test.hasNext(), is(false));
    }
}