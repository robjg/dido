package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.data.immutable.MapData;
import dido.data.schema.SchemaBuilder;
import dido.data.util.FieldValuesOut;
import dido.how.DataIn;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
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
    void withSchema() {

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
    void pickColumns() {

        String records =
                "Apple,5,19.50" + System.lineSeparator() +
                        "Orange,2,35.24" + System.lineSeparator();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamedAt(2, "Quantity", int.class)
                .addNamedAt( 3, "Price", double.class)
                .build();

        List<DidoData> expected = ArrayData.withSchema(schema)
                .many()
                .of(5, 19.5)
                .of(2, 35.24)
                .toList();

        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .fromReader(new StringReader(records))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
            assertThat(results.getFirst().getSchema(), is(schema));
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

        List<DidoData> results;

        try (DataIn in = DataInCsv.with()
                .partialSchema(someSchema)
                .fromReader(new StringReader(records))) {

            results = in.stream().collect(Collectors.toList());

        }

        assertThat(results, is(expected));
        assertThat(results.get(0).getSchema(), is(schema));

        List<Object[]> values = results.stream()
                .map(FieldValuesOut.forSchema(schema)::toArray)
                .toList();

        assertThat(values, contains(new Object[]{"Apple", 5, 19.5}, new Object[]{"Orange", 2, 35.24}));

    }

    @Test
    void outOfOrderHeader() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("InStock", boolean.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("BestBefore", LocalDate.class)
                .addNamed("Fruit", String.class)
                .build();

        List<DidoData> expected = ArrayData.withSchema(schema)
                .many()
                .of(null, 5, 19.5, LocalDate.of(2025, 10, 16), "Apple")
                .of(null, 2, 35.24, LocalDate.of(2025, 10, 24), "Orange")
                .of(null, 3, 17.65, LocalDate.of(2025, 10, 13), "Banana")
                .toList();

        List<DidoData> results;

        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .conversion(String.class, LocalDate.class, ((String s) -> LocalDate.parse(s)))
                .make();

        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .header(true)
                .conversionProvider(conversionProvider)
                .fromInputStream(Objects.requireNonNull(
                        getClass().getResourceAsStream("/data/FruitWithHeader.csv")))) {

            results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
            assertThat(results.getFirst().getSchema(), is(schema));
        }

        DidoData row1 = results.getFirst();
        assertThat(row1.hasNamed("InStock"), is(false));
        assertThat(row1.getStringNamed("Fruit"), is("Apple"));
        assertThat(row1.getIntNamed("Qty"), is(5));
        assertThat(row1.getShortNamed("Qty"), is((short) 5));
        assertThat(row1.getByteNamed("Qty"), is((byte) 5));
        assertThat(row1.getLongNamed("Qty"), is(5L));
        assertThat(row1.getDoubleNamed("Price"), is(19.5));
        assertThat(row1.getFloatNamed("Price"), is(19.5F));
        assertThat(row1.getNamed("BestBefore"), is(LocalDate.of(2025, 10, 16)));
        assertThat(row1.hasAt(1), is(false));
        assertThat(row1.getStringAt(5), is("Apple"));
        assertThat(row1.getIntAt(2), is(5));
        assertThat(row1.getShortAt(2), is((short) 5));
        assertThat(row1.getByteAt(2), is((byte) 5));
        assertThat(row1.getLongAt(2), is(5L));
        assertThat(row1.getDoubleAt(3), is(19.5));
        assertThat(row1.getFloatAt(3), is(19.5F));
        assertThat(row1.getAt(4), is(LocalDate.of(2025, 10, 16)));

        List<Object[]> values = results.stream()
                .map(FieldValuesOut.forSchema(results.getFirst().getSchema())::toArray)
                .toList();

        assertThat(values, contains(
                new Object[]{null, 5, 19.5, LocalDate.of(2025, 10, 16), "Apple"},
                new Object[]{null, 2, 35.24, LocalDate.of(2025, 10, 24), "Orange"},
                new Object[]{null, 3, 17.65, LocalDate.of(2025, 10, 13), "Banana"}));

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