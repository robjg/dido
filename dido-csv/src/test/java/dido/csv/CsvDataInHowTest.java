package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.SchemaBuilder;
import dido.how.DataIn;
import dido.how.DataInHow;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CsvDataInHowTest {

    @Test
    void testWithDefaults() throws Exception {

        DataInHow<InputStream, DidoData> test = CsvDataInHow.withDefaultOptions();

        String records =
                "Apple,5,19.50" + System.lineSeparator() +
                "Orange,2,35.24" + System.lineSeparator();

        DataIn<DidoData> supplier = test.inFrom(
                new ByteArrayInputStream(records.getBytes()));

        {
            DidoData data = supplier.get();

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
            DidoData data = supplier.get();

            assertThat(data.getStringAt(1), is("Orange"));
            assertThat(data.getStringAt(2), is("2"));
            assertThat(data.getStringAt(3), is("35.24"));
        }

        assertThat(supplier.get(), nullValue());
    }

    @Test
    void testWithSchema() throws Exception {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Type", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        DataInHow<InputStream, DidoData> test = CsvDataInHow.with()
                .schema(schema)
                .make();

        DataIn<DidoData> supplier = test.inFrom(
                new ByteArrayInputStream("Apple,5,27.2".getBytes()));

        {
            DidoData data = supplier.get();

            assertThat(data.getStringNamed("Type"), is("Apple"));
            assertThat(data.getIntNamed("Quantity"), is(5));
            assertThat(data.getDoubleNamed("Price"), is(27.2));

            assertThat(data.getNamed("Quantity"), is(5));
            assertThat(data.getNamed("Price"), is(27.2));
        }

        assertThat(supplier.get(), nullValue());
    }

    @Test
    void testWithPartialSchema() throws Exception {

        String records = "Fruit,Quantity,Price" + System.lineSeparator() +
                "Apple,5,19.50" + System.lineSeparator() +
                "Orange,2,35.24" + System.lineSeparator();

        DataSchema someSchema = SchemaBuilder.newInstance()
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        DataInHow<InputStream, DidoData> test = CsvDataInHow.with()
                .schema(someSchema)
                .partialSchema(true)
                .make();

        DataIn<DidoData> supplier = test.inFrom(
                new ByteArrayInputStream(records.getBytes()));

        {
            DidoData data = supplier.get();

            DataSchema schema = data.getSchema();

            assertThat(schema.getFieldNames(), Matchers.contains("Fruit","Quantity","Price"));
            assertThat(schema.lastIndex(), is(3));
            assertThat(schema.getTypeAt(1), is(String.class));
            assertThat(schema.getTypeAt(2), is(int.class));
            assertThat(schema.getTypeAt(3), is(double.class));

            assertThat(data.getStringNamed("Fruit"), is("Apple"));
            assertThat(data.getIntNamed("Quantity"), is(5));
            assertThat(data.getDoubleNamed("Price"), is(19.50));
        }

        {
            DidoData data = supplier.get();

            assertThat(data.getStringAt(1), is("Orange"));
            assertThat(data.getIntAt(2), is(2));
            assertThat(data.getDoubleAt(3), is(35.24));
        }

        assertThat(supplier.get(), nullValue());
    }

    @Test
    void testEmptyValues() throws Exception {

        DataIn<DidoData> dataIn = CsvDataInHow.withDefaultOptions()
                .inFrom(new ByteArrayInputStream(",,".getBytes(StandardCharsets.UTF_8)));

        DidoData data = dataIn.get();

        DataSchema schema = data.getSchema();

        assertThat(schema.lastIndex(), is(3));

        assertThat(data.getStringAt(1), is(""));
        assertThat(data.getStringAt(2), is(""));
        assertThat(data.getStringAt(3), is(""));

        assertThat(dataIn.get(), nullValue());
    }

    @Test
    void testOneAheadIterator() {

        Iterator<String> original = List.of("one", "two", "three").iterator();

        assertThat(original.hasNext(), is(true));

        String first = original.next();
        assertThat(first, is("one"));

        Iterator<String> test = new CsvDataInHow.OneAheadIterator<>(original, first);

        assertThat(test.hasNext(), is(true));
        assertThat(test.next(), is("one"));

        assertThat(test.hasNext(), is(true));
        assertThat(test.next(), is("two"));

        assertThat(test.hasNext(), is(true));
        assertThat(test.next(), is("three"));

        assertThat(test.hasNext(), is(false));
    }
}