package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.examples.objects.Apple;
import dido.examples.objects.FruitBean;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.objects.DataInObjects;
import dido.objects.DataOutObjects;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class ObjectExamplesTest {

    @Test
    void serialize() {

        // #serializeSingle{
        DidoData didoData = DataInObjects
                .beanOf(Apple.class)
                .toDidoData(new Apple());

        assertThat(didoData.getStringNamed("fruit"), is("Apple"));
        assertThat(didoData.getIntNamed("qty"), is(5));
        assertThat(didoData.getDoubleNamed("price"), is(19.5));
        // }#serializeSingle
    }

    @Test
    void serializeWithFields() {

        // #serializeFields{
        DidoData didoData = DataInObjects
                .beanOf(Apple.class)
                .fields("fruit", "qty", "price")
                .toDidoData(new Apple());

        assertThat(didoData, is(DidoData.of("Apple", 5, 19.5)));
        assertThat(didoData.getSchema().toString(), is("{[1:fruit]=java.lang.String, [2:qty]=int, [3:price]=double}"));
        // }#serializeFields
    }

    @Test
    void asDataIn() {

        // #dataIn{
        try (DataIn dataIn = DataInObjects
                .beanOf(Apple.class)
                .fields("fruit", "qty", "price")
                .inFrom(List.of(new Apple(), new Apple(), new Apple()))) {

            List<DidoData> didoData = dataIn
                .stream().collect(Collectors.toList());

            assertThat(didoData, contains(
                    DidoData.of("Apple", 5, 19.5),
                    DidoData.of("Apple", 5, 19.5),
                    DidoData.of("Apple", 5, 19.5)));
        }
        // }#dataIn

    }

    @Test
    void asStream() {

        // #serializeStream{
        List<DidoData> didoData = Stream.of(new Apple(), new Apple(), new Apple())
                .map(DataInObjects
                        .beanOf(Apple.class)
                        .fields("fruit", "qty", "price")
                        .mapper())
                .collect(Collectors.toList());
        // }#serializeStream

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.5),
                DidoData.of("Apple", 5, 19.5),
                DidoData.of("Apple", 5, 19.5)));


    }

    @Test
    void deserialize() {

        // #deserializeSingle{
        DidoData didoData = DidoData.builder()
                .withString("fruit", "Apple")
                .withInt("qty", 5)
                .withDouble("price", 19.5)
                .build();

        FruitBean fruit = DataOutObjects
                .beanOf(FruitBean.class)
                .fromDidoData(didoData);

        assertThat(fruit.toString(), is("FruitBean{fruit='Apple', qty=5, price=19.5}"));
        // }#deserializeSingle
    }

    @Test
    void deserializeStream() {

        // #deserializeList{
        List<FruitBean> fruitBeans = new LinkedList<>();

        DataSchema schema = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        List<DidoData> didoData = DidoData.valuesWithSchema(schema)
                .many()
                .of("Apple", 5, 19.5)
                .of("Orange", 10, 31.6)
                .of("Pear", 7, 22.1)
                .toList();

        try (DataOut dataOut = DataOutObjects
                .beanOf(FruitBean.class)
                .schema(schema)
                .<FruitBean>outTo(fruitBeans::add)) {

            didoData.forEach(dataOut);
        }

        assertThat(fruitBeans.stream()
                        .map(Objects::toString)
                        .collect(Collectors.toList()),
                contains(
                        "FruitBean{fruit='Apple', qty=5, price=19.5}",
                        "FruitBean{fruit='Orange', qty=10, price=31.6}",
                        "FruitBean{fruit='Pear', qty=7, price=22.1}"));
        // }#deserializeList

        // #deserializeStream{
        fruitBeans = didoData.stream()
                .map(DataOutObjects
                        .beanOf(FruitBean.class)
                        .schema(schema)
                        .<FruitBean>mapper())
                .collect(Collectors.toList());
        // }#deserializeStream

        assertThat(fruitBeans.stream()
                        .map(Objects::toString)
                        .collect(Collectors.toList()),
                contains(
                        "FruitBean{fruit='Apple', qty=5, price=19.5}",
                        "FruitBean{fruit='Orange', qty=10, price=31.6}",
                        "FruitBean{fruit='Pear', qty=7, price=22.1}"));

    }
}
