package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.DataInPoi;
import dido.poi.DataOutPoi;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class PoiExamplesTest {

    @Test
    void simpleReadExample() throws IOException {

        try (InputStream in = Objects.requireNonNull(
                getClass().getResourceAsStream("/data/SimpleTableNoHeadings.xlsx"));
             OutputStream out = Files.newOutputStream(Path.of("Fruit.xlsx"))) {

            in.transferTo(out);
        }

        // #snippet1{
        List<DidoData> didoData;

        try (DataIn in = DataInPoi.fromPath(Path.of("Fruit.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5.0, 19.50),
                DidoData.of("Orange", 2.0, 35.24),
                DidoData.of("Pear", 3.0, 26.84)));
        // }#snippet1

        // #snippet2{
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:f_1]=java.lang.String, [2:f_2]=java.lang.Double, [3:f_3]=java.lang.Double}"));
        // }#snippet2

        // #snippet3{
        try (DataIn in = DataInPoi.with()
                .partialSchema(DataSchema.builder()
                        .addAt(2, int.class)
                        .build())
                .fromPath(Path.of("Fruit.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.50),
                DidoData.of("Orange", 2, 35.24),
                DidoData.of("Pear", 3, 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:f_1]=java.lang.String, [2:f_2]=int, [3:f_3]=java.lang.Double}"));
        // }#snippet3

        // #snippet4{
        try (DataIn in = DataInPoi.with()
                .schema(DataSchema.builder()
                        .addNamedAt(1, "Fruit", String.class)
                        .addNamedAt(3, "Price", double.class)
                        .build())
                .fromPath(Path.of("Fruit.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 19.50),
                DidoData.of("Orange", 35.24),
                DidoData.of("Pear", 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:Fruit]=java.lang.String, [3:Price]=double}"));
        // }#snippet4

        Files.delete(Path.of("Fruit.xlsx"));
    }

    @Test
    void readWithHeadingsExample() throws IOException {

        try (InputStream in = Objects.requireNonNull(
                getClass().getResourceAsStream("/data/SimpleTableWithHeadings.xlsx"));
             OutputStream out = Files.newOutputStream(Path.of("FruitWithHeadings.xlsx"))) {

            in.transferTo(out);
        }

        // #snippet5{
        List<DidoData> didoData;

        try (DataIn in = DataInPoi.with()
                .header(true)
                .fromPath(Path.of("FruitWithHeadings.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5.0, 19.50),
                DidoData.of("Orange", 2.0, 35.24),
                DidoData.of("Pear", 3.0, 26.84)));
        // }#snippet5

        // #snippet6{
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:Fruit]=java.lang.String, [2:Quantity]=java.lang.Double, [3:Price]=java.lang.Double}"));
        // }#snippet6

        // #snippet7{
        try (DataIn in = DataInPoi.with()
                .partialSchema(DataSchema.builder()
                        .addNamedAt(2, "Quantity", int.class)
                        .build())
                .header(true)
                .fromPath(Path.of("FruitWithHeadings.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        System.out.println(didoData.get(0).getSchema().toString());
        System.out.println(didoData);

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.50),
                DidoData.of("Orange", 2, 35.24),
                DidoData.of("Pear", 3, 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:Fruit]=java.lang.String, [2:Quantity]=int, [3:Price]=java.lang.Double}"));
        // }#snippet7

        Files.delete(Path.of("FruitWithHeadings.xlsx"));
    }

    @Test
    void simpleWriteExample() throws IOException {

        // #snippet8{
        List<DidoData> didoData = List.of(
                DidoData.of("Apple", 5.0, 19.50),
                DidoData.of("Orange", 2.0, 35.24),
                DidoData.of("Pear", 3.0, 26.84));

        try (DataOut out = DataOutPoi.toPath(Path.of("Fruit.xlsx"))) {
            didoData.forEach(out);
        }
        // }#snippet8

        List<DidoData> result;

        try (DataIn in = DataInPoi.fromPath(Path.of("Fruit.xlsx"))) {

            result = in.stream().collect(Collectors.toList());
        }

        assertThat(result, is(didoData));

        Files.delete(Path.of("Fruit.xlsx"));
    }

    @Test
    void writeWithHeadingsExample() throws IOException {

        // #snippet9{
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> didoData = DidoData.valuesWithSchema(schema)
                .many()
                .of("Apple", 5, 19.50)
                .of("Orange", 2, 35.24)
                .of("Pear", 3, 26.84)
                .toList();

        try (DataOut out = DataOutPoi.with()
                .header(true)
                .toPath(Path.of("FruitWithHeadings.xlsx"))) {
            didoData.forEach(out);
        }
        // }#snippet9

        List<DidoData> result;

        try (DataIn in = DataInPoi.with()
                .header(true)
                .schema(schema)
                .fromPath(Path.of("FruitWithHeadings.xlsx"))) {

            result = in.stream().collect(Collectors.toList());
        }

        assertThat(result, is(didoData));

        Files.delete(Path.of("FruitWithHeadings.xlsx"));
    }
}
