package dido.examples;

import com.google.gson.*;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import dido.json.DataInJson;
import dido.json.DataOutJson;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        try (DataIn in = DataInJson
                .with().strictness(Strictness.LENIENT)
                .fromPath(Path.of("Fruit.json"))) {

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
                is("{[1:Fruit]=java.lang.String, [2:Qty]=double, [3:Price]=double}"));
        // }#snippet2

        // #snippet3{
        try (DataIn in = DataInJson.with()
                .strictness(Strictness.LENIENT)
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
                is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=double}"));
        // }#snippet3

        // #snippet4{
        try (DataIn in = DataInJson.with()
                .strictness(Strictness.LENIENT)
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
        // }#snippet4

        Files.delete(Path.of("Fruit.json"));
    }

    @Test
    void nullsAndNans() throws JSONException {

        // #missingJsonFieldIn{
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        DidoData data = DataInJson.with()
                .schema(schema)
                .mapFromString()
                .apply("{ 'Fruit': 'Apple', 'Price': 19.50 }");

        assertThat(data, is(DidoData.withSchema(schema)
                .of("Apple", null, 19.50)));
        // }#missingJsonFieldIn

        // #nullDidoFieldOut{
        String json = DataOutJson.with()
                .schema(schema)
                .mapToString()
                .apply(data);

        JSONAssert.assertEquals("{ 'Fruit': 'Apple', 'Price': 19.50 }", json,
                JSONCompareMode.STRICT);
        // }#nullDidoFieldOut

        // #serializeNulls{
        String jsonWithNull = DataOutJson.with()
                .schema(schema)
                .serializeNulls()
                .mapToString()
                .apply(data);

        JSONAssert.assertEquals("{ 'Fruit': 'Apple', Qty: null, 'Price': 19.50 }", jsonWithNull,
                JSONCompareMode.STRICT);
        // }#serializeNulls

        DidoData dataWithNull = DataInJson.with()
                .schema(schema)
                .mapFromString()
                .apply("{ 'Fruit': 'Apple', 'Qty': null, 'Price': 19.50 }");

        assertThat(dataWithNull, is(DidoData.withSchema(schema)
                .of("Apple", null, 19.50)));

        assertThat(data.hasNamed("Qty"), is(false));
    }

    @Test
    void conversions() throws JSONException {

        // #conversionJson{
        String json = "{ 'Fruit': 'Apple', 'BestBefore': '2025-02-14' }";
        // }#conversionJson

        // #conversionDeserializer{
        class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
            }
        }

        DidoData data = DataInJson.with()
                .gsonBuilder(gsonBuilder ->
                        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()))
                .partialSchema(DataSchema.builder()
                        .addNamed("BestBefore", LocalDate.class)
                        .build())
                .mapFromString()
                .apply(json);

        assertThat(data, is(DidoData.of("Apple", LocalDate.parse("2025-02-14"))));
        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:BestBefore]=java.time.LocalDate}"));
        // }#conversionDeserializer

        // #conversionSerializer{
        class LocalDateSerializer implements JsonSerializer<LocalDate> {
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE));
            }
        }

        String jsonAgain = DataOutJson.with()
                .gsonBuilder(gsonBuilder ->
                        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer()))
                .mapToString()
                .apply(data);

        JSONAssert.assertEquals("{ 'Fruit': 'Apple', 'BestBefore': '2025-02-14' }", jsonAgain,
                JSONCompareMode.STRICT);
        // }#conversionSerializer
    }

    @Test
    void didoConversions() throws JSONException {

        String json = "{ 'Fruit': 'Apple', 'BestBefore': '2025-02-14' }";

        // #conversionDido{
        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .<String, LocalDate>conversion(String.class, LocalDate.class, LocalDate::parse)
                .make();
        // }#conversionDido

        // #conversionDidoIn{
        DidoData data = DataInJson.with()
                .conversionProvider(conversionProvider)
                .didoConversion(String.class, LocalDate.class)
                .partialSchema(DataSchema.builder()
                        .addNamed("BestBefore", LocalDate.class)
                        .build())
                .mapFromString()
                .apply(json);

        assertThat(data, is(DidoData.of("Apple", LocalDate.parse("2025-02-14"))));
        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:BestBefore]=java.time.LocalDate}"));
        // }#conversionDidoIn

        // #conversionDidoOut{
        String jsonAgain = DataOutJson.with()
                .conversionProvider(conversionProvider)
                .didoConversion(LocalDate.class, String.class)
                .mapToString()
                .apply(data);

        JSONAssert.assertEquals("{ 'Fruit': 'Apple', 'BestBefore': '2025-02-14' }", jsonAgain,
                JSONCompareMode.STRICT);
        // }#conversionDidoOut
    }
}

