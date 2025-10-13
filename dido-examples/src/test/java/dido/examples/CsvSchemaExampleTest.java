package dido.examples;

import dido.csv.DataInCsv;
import dido.csv.DataOutCsv;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.operators.transform.DidoTransform;
import dido.operators.transform.FieldViews;
import dido.operators.transform.ViewTransformBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CsvSchemaExampleTest {

    @Test
    void disparateIndices() {

        // #usingIndicesIn{
        DataSchema schema = DataSchema.builder()
                .addNamedAt(1,"Fruit", String.class)
                .addNamedAt(3, "Price", double.class)
                .build();

        DidoData data = DataInCsv.with()
                .schema(schema)
                .mapFromString()
                .apply("Apple, 5, 23.5");

        assertThat(data,
                is(DidoData.withSchema(schema).of("Apple", 23.5)));
        // }#usingIndicesIn

        // #usingIndicesOut{
        String csv = DataOutCsv.with()
                .schema(schema)
                .mapToString()
                .apply(data);

        assertThat(csv,
                is("Apple,23.5"));
        // }#usingIndicesOut

        // #blankColumns{
        DidoTransform transform = ViewTransformBuilder
                .with()
                .existingFields(true)
                .forSchema(data.getSchema())
                .addFieldView(FieldViews.setNamedAt(2, "Qty", null, int.class))
                .build();

        String csv2 = DataOutCsv.with()
                .schema(transform.getResultantSchema())
                .mapToString()
                .apply(transform.apply(data));

        assertThat(csv2,
                is("Apple,,23.5"));
        // }#blankColumns

    }

}
