package dido.text;

import dido.data.ArrayData;
import dido.data.DidoData;
import dido.data.enums.EnumMapData;
import dido.data.generic.GenericDataBuilder;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;

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

}