package dido.examples;

import dido.data.DidoData;
import dido.examples.objects.Apple;
import dido.objects.DataInObjects;
import org.junit.jupiter.api.Test;

import java.util.List;
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
    void serializeStream() {

        // #serializeStream{
        List<DidoData> didoData = DataInObjects
                .beanOf(Apple.class)
                .fields("fruit", "qty", "price")
                .inFrom(Stream.of(new Apple(), new Apple(), new Apple()))
                .stream().collect(Collectors.toList());

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.5),
                DidoData.of("Apple", 5, 19.5),
                DidoData.of("Apple", 5, 19.5)));
        // }#serializeStream
    }
}
