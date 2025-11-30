package dido.text;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.enums.EnumMapData;
import dido.data.generic.GenericDataBuilder;
import dido.data.immutable.ArrayData;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataOutTextTableTest {

    @Test
    void testSimple() throws Exception {

        String expected =
                "f_1       |f_2|   f_3" + System.lineSeparator() +
                        "----------+---+------" + System.lineSeparator() +
                        "Apple     |  5|  22.3" + System.lineSeparator() +
                        "Cantaloupe| 27| 245.3" + System.lineSeparator() +
                        "Pear      |232|11.328" + System.lineSeparator();

        DidoData data1 = ArrayData.of("Apple", 5, 22.3);
        DidoData data2 = ArrayData.of("Cantaloupe", 27, 245.3);
        DidoData data3 = ArrayData.of("Pear", 232, 11.328);

        StringBuilder output = new StringBuilder();

        DataOut out = DataOutTextTable.with()
                .schema(data1.getSchema())
                .make()
                .outTo(output);

        out.accept(data1);
        out.accept(data2);
        out.accept(data3);

        out.close();

        assertThat(output.toString(), is(expected));
    }

    enum Fruit {
        Fruit,
        Quantity,
        Price
    }

    @Test
    void testWithFields() {

        String expected =
                "Fruit     |Quantity| Price" + System.lineSeparator() +
                        "----------+--------+------" + System.lineSeparator() +
                        "Apple     |       5|  22.3" + System.lineSeparator() +
                        "Cantaloupe|      27| 245.3" + System.lineSeparator() +
                        "Pear      |     232|11.328" + System.lineSeparator();

        GenericDataBuilder<Fruit> builder = EnumMapData.builderForEnum(Fruit.class);

        DidoData data1 =
                builder.withString(Fruit.Fruit, "Apple")
                        .withInt(Fruit.Quantity, 5)
                        .withDouble(Fruit.Price, 22.3)
                        .build();

        DidoData data2 = ArrayData.of("Cantaloupe", 27, 245.3);
        DidoData data3 = ArrayData.of("Pear", 232, 11.328);

        StringBuilder output = new StringBuilder();

        DataOut out = DataOutTextTable.with()
                .schema(data1.getSchema())
                .make()
                .outTo(output);

        out.accept(data1);
        out.accept(data2);
        out.accept(data3);

        out.close();

        assertThat(output.toString(), is(expected));
    }

    @Test
    void withDisparateField() {

        String expected =
                "Fruit     |Quantity| Price" + System.lineSeparator() +
                        "----------+--------+------" + System.lineSeparator() +
                        "Apple     |       5|  22.3" + System.lineSeparator() +
                        "Cantaloupe|      27| 245.3" + System.lineSeparator() +
                        "Pear      |     232|11.328" + System.lineSeparator();

        DataSchema schema = DataSchema.builder()
                .addNamedAt(5, "Fruit", String.class)
                .addNamedAt(7, "Quantity", int.class)
                .addNamedAt(20, "Price", double.class)
                .build();

        List<DidoData> data = DidoData.withSchema(schema)
                .many()
                .of("Apple", 5, 22.3)
                .of("Cantaloupe", 27, 245.3)
                .of("Pear", 232, 11.328)
                .toList();

        StringBuilder output = new StringBuilder();

        try (DataOut out = DataOutTextTable.with()
                .schema(schema)
                .make()
                .outTo(output)) {

            data.forEach(out);
        }

        assertThat(output.toString(), is(expected));
    }
}