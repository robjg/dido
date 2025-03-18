package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.examples.objects.Apple;
import dido.objects.DataInObjects;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
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
    void serializeWithSchema() {

        // #serializeSchema{
        DataSchema schema = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("price", double.class)
                .build();

        DidoData didoData = DataInObjects
                .beanOf(Apple.class)
                .schema(schema)
                .toDidoData(new Apple());

        assertThat(didoData, is(DidoData.of("Apple", 19.5)));
        assertThat(didoData.getSchema().toString(), is("{[1:fruit]=java.lang.String, [2:price]=double}"));
        // }#serializeSchema
    }

    @Test
    void serializeList() {

        // #serializeList{
        List<DidoData> didoData = DataInObjects
                .beanOf(Apple.class)
                .make()
                .inFrom(Stream.of(new Apple(), new Apple(), new Apple()))
                .stream().collect(Collectors.toList());

        assertThat(didoData.get(0).getStringNamed("fruit"), is("Apple"));
        assertThat(didoData.get(1).getIntNamed("qty"), is(5));
        assertThat(didoData.get(2).getDoubleNamed("price"), is(19.5));
        // }#serializeList
    }
}
