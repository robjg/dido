package dido.text;

import dido.data.ArrayData;
import dido.data.DidoData;
import dido.data.enums.EnumMapData;
import dido.data.generic.GenericDataBuilder;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TextTableOutTest {

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

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        DataOut out = TextTableOut.ofOptions()
                .schema(data1.getSchema())
                .create()
                .outTo(output);

        out.accept(data1);
        out.accept(data2);
        out.accept(data3);

        out.close();

        byte[] a = expected.getBytes(StandardCharsets.UTF_8);
        byte[] b = output.toByteArray();

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                System.out.println(i + ": " + a[i] + " " + b[i]);
            }
        }

        assertThat(output.toString(StandardCharsets.UTF_8), is(expected));

        assertThat(output.toByteArray(), is(expected.getBytes(StandardCharsets.UTF_8)));
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

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        DataOut out = TextTableOut.ofOptions()
                .schema(data1.getSchema())
                .create()
                .outTo(output);

        out.accept(data1);
        out.accept(data2);
        out.accept(data3);

        out.close();

        assertThat(output.toString(), is(expected));
    }

}