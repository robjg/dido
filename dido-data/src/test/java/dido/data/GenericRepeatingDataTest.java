package dido.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class GenericRepeatingDataTest {

    @Test
    void testEqualsAndHashCode() {

        GenericRepeatingData<String> data1 = GenericRepeatingData.of(
                GenericMapData.of("Fruit", "Apple", "Qty", 5),
                GenericMapData.of("Fruit", "Pear", "Qty", 3));

        GenericRepeatingData<String> data2 = GenericRepeatingData.of(
                List.of(
                        GenericMapData.of("Fruit", "Apple", "Qty", 5),
                        GenericMapData.of("Fruit", "Pear", "Qty", 3)));

        assertThat(data1, is(data2));
        assertThat(data1.hashCode(), is(data2.hashCode()));
    }

    @Test
    void testToString() {

        GenericRepeatingData<String> data1 = GenericRepeatingData.of(
                GenericMapData.of("Fruit", "Apple", "Qty", 5),
                GenericMapData.of("Fruit", "Pear", "Qty", 3));

        assertThat(data1.toString(), is("[{[Fruit]=Apple, [Qty]=5}, {[Fruit]=Pear, [Qty]=3}]"));

        GenericRepeatingData<String> data2 = GenericRepeatingData.of(
                List.of(
                        GenericMapData.of("Fruit", "Apple", "Qty", 5),
                        GenericMapData.of("Fruit", "Pear", "Qty", 3)));

        assertThat(data2.toString(), is("[{[Fruit]=Apple, [Qty]=5}, {[Fruit]=Pear, [Qty]=3}]"));
    }
}
