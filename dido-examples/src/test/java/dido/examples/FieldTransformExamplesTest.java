package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.operators.transform.FieldOps;
import dido.operators.transform.Transformation;
import dido.operators.transform.TransformationBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class FieldTransformExamplesTest {

    @Test
    void addRemoveField() {

        // #snippet1{
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

        LocalDate bestBeforeDate = LocalDate.parse("2025-01-14");

        Transformation transformation = TransformationBuilder.with()
                .copy(true)
                .forSchema(schema)
                .addOp(FieldOps.removeNamed("Quantity"))
                .addOp(FieldOps.setNamed("BestBefore", bestBeforeDate))
                .addOp(FieldOps.setIntNamed("Markup", 20))
                .build();

        List<DidoData> results = didoData.stream()
                .map(transformation)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("Apple", 19.50, bestBeforeDate, 20),
                DidoData.of("Orange", 35.24, bestBeforeDate, 20),
                DidoData.of("Pear", 26.84, bestBeforeDate, 20)));
        // }#snippet1

    }
}
