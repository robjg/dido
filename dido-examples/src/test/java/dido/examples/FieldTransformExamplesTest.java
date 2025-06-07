package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.ReadStrategy;
import dido.data.util.DataBuilder;
import dido.operators.transform.DidoTransform;
import dido.operators.transform.FieldViews;
import dido.operators.transform.WriteTransformBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FieldTransformExamplesTest {

    // #snippetGiven{
    DataSchema fromSchema = DataSchema.builder()
            .addNamed("Fruit", String.class)
            .addNamed("Quantity", int.class)
            .addNamed("Price", double.class)
            .build();

    List<DidoData> didoData = DidoData.valuesWithSchema(fromSchema)
            .many()
            .of("Apple", 5, 19.50)
            .of("Orange", 2, 35.24)
            .of("Pear", 3, 26.84)
            .toList();
    // }#snippetGiven


    @Test
    void theLongWay() {

        // #snippetBuilder{
        LocalDate bestBeforeDate = LocalDate.parse("2025-01-14");

        Function<DidoData, DidoData> mappingFunc = data -> DataBuilder.newInstance()
                .with("Fruit", data.getStringNamed("Fruit"))
                .with("Qty", data.getIntNamed("Quantity"))
                .with("DiscountPrice", data.getDoubleNamed("Price") * .9)
                .with("BestBefore", bestBeforeDate)
                .build();

        List<DidoData> results = didoData.stream()
                .map(mappingFunc)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("Apple", 5, 17.55, bestBeforeDate),
                DidoData.of("Orange", 2, 31.716, bestBeforeDate),
                DidoData.of("Pear", 3, 24.156, bestBeforeDate)));
        // }#snippetBuilder
    }

    @Test
    void withFieldTransformation() {

        LocalDate bestBeforeDate = LocalDate.parse("2025-01-14");

        // #snippet1{

        DidoTransform transform = WriteTransformBuilder.with()
                .existingFields(true)
                .reIndex(true)
                .forSchema(fromSchema)
                .addFieldView(FieldViews.rename("Quantity", "Qty"))
                .addFieldView(FieldViews.map().from("Price").to("DiscountPrice")
                        .with().doubleOp(price -> price * .9))
                .addFieldView(FieldViews.removeNamed("Price"))
                .addFieldView(FieldViews.setNamed("BestBefore", bestBeforeDate))
                .build();

        List<DidoData> results = didoData.stream()
                .map(transform)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("Apple", 5, 17.55, bestBeforeDate),
                DidoData.of("Orange", 2, 31.716, bestBeforeDate),
                DidoData.of("Pear", 3, 24.156, bestBeforeDate)));
        // }#snippet1

        // #snippetResultSchema{
        DataSchema resultantSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("DiscountPrice", double.class)
                .addNamed("BestBefore", LocalDate.class)
                .build();

        assertThat(transform.getResultantSchema(), is(resultantSchema));

        assertThat(transform.getResultantSchema(), instanceOf(ReadStrategy.class));
        // }#snippetResultSchema
    }
}
