package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.json.DataInJson;
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

class JsonExamplesTest {

    @Test
    void simpleReadExample() throws IOException {

        try (InputStream in = Objects.requireNonNull(
                getClass().getResourceAsStream("/data/FruitLines.json"));
             OutputStream out = Files.newOutputStream(Path.of("Fruit.json"))) {

            in.transferTo(out);
        }

        // #snippet1{
        List<DidoData> didoData;

        try (DataIn in = DataInJson.fromPath(Path.of("Fruit.json"))) {

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
                is("{[1:Fruit]=java.lang.String, [2:Qty]=java.lang.Double, [3:Price]=java.lang.Double}"));
        // }#snippet2

        // #snippet3{
        try (DataIn in = DataInJson.with()
                .partialSchema(DataSchema.builder()
                        .addNamed("Qty", int.class)
                        .build())
                .fromPath(Path.of("Fruit.json"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.50),
                DidoData.of("Orange", 2, 35.24),
                DidoData.of("Pear", 3, 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=java.lang.Double}"));
        // }#snippet3

        // #snippet4{
        try (DataIn in = DataInJson.with()
                .schema(DataSchema.builder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Price", double.class)
                        .build())
                .fromPath(Path.of("Fruit.json"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 19.50),
                DidoData.of("Orange", 35.24),
                DidoData.of("Pear", 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:Fruit]=java.lang.String, [2:Price]=double}"));
        // }#snippet3

        Files.delete(Path.of("Fruit.json"));
    }
}
