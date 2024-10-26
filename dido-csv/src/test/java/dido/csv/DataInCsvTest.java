package dido.csv;

import dido.data.*;
import dido.how.DataIn;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
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

        DataIn dataIn = DataInCsv.fromReader(new StringReader(records));

        List<DidoData> results = dataIn.stream().collect(Collectors.toList());

        assertThat(results.size(), is(2));

        {
            DidoData data = results.get(0);

            DataSchema schema = data.getSchema();

            assertThat(schema.getFieldNames(), contains("f_1", "f_2", "f_3"));
            assertThat(schema.lastIndex(), is(3));
            assertThat(schema.getTypeAt(1), is(String.class));
            assertThat(schema.getTypeAt(3), is(String.class));

            assertThat(data.getStringAt(1), is("Apple"));
            assertThat(data.getStringAt(2), is("5"));
            assertThat(data.getStringAt(3), is("19.50"));
        }

        {
            DidoData data = results.get(1);

            assertThat(data.getStringAt(1), is("Orange"));
            assertThat(data.getStringAt(2), is("2"));
            assertThat(data.getStringAt(3), is("35.24"));
        }

    }

    @Test
    void testWithSchema() {

        String records =
                "Apple,5,19.50" + System.lineSeparator() +
                        "Orange,2,35.24" + System.lineSeparator();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Type", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> expected = ArrayData.valuesForSchema(schema)
                .many()
                .of("Apple", 5, 19.5)
                .of( "Orange", 2, 35.24)
                .toList();

        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .fromReader(new StringReader(records))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
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

        List<DidoData> results;

        try (DataIn in = DataInCsv.with()
                .schema(someSchema)
                .partialSchema(true)
                .fromReader(new StringReader(records))) {
            results = in.stream().collect(Collectors.toList());
        }
        assertThat(results.size(), is(2));

        {
            DidoData data = results.get(0);

            DataSchema schema = data.getSchema();

            assertThat(schema.getFieldNames(), Matchers.contains("Fruit", "Quantity", "Price"));
            assertThat(schema.lastIndex(), is(3));
            assertThat(schema.getTypeAt(1), is(String.class));
            assertThat(schema.getTypeAt(2), is(int.class));
            assertThat(schema.getTypeAt(3), is(double.class));

            assertThat(data.getStringNamed("Fruit"), is("Apple"));
            assertThat(data.getIntNamed("Quantity"), is(5));
            assertThat(data.getDoubleNamed("Price"), is(19.50));
        }

        {
            DidoData data = results.get(1);

            assertThat(data.getStringAt(1), is("Orange"));
            assertThat(data.getIntAt(2), is(2));
            assertThat(data.getDoubleAt(3), is(35.24));
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
    void asMapperFromString() {

        DidoData data = DataInCsv.asMapperFromString()
                .apply("Apple,5,27.2");

        DidoData expected = MapData.newBuilderNoSchema()
                .withString("f_1", "Apple")
                .withString("f_2", "5")
                .withString("f_3", "27.2")
                .build();

        assertThat(data, is(expected));
    }

    @Test
    void asMapperFromStringWithSchema() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Type", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        DidoData data = DataInCsv.with()
                .schema(schema).asMapperFromString()
                .apply("Apple,5,27.2");

        DidoData expected = MapData.newBuilderNoSchema()
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