package dido.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RepeatingDataTest {

    @Test
    void testEqualsAndHashCode() {

        RepeatingData data1 = RepeatingData.of(
                ArrayData.of("Apple", 5), ArrayData.of("Pear", 3));

        RepeatingData data2 = RepeatingData.of(
                List.of(ArrayData.of("Apple", 5), ArrayData.of("Pear", 3)));

        assertThat(data1, is(data2));
        assertThat(data1.hashCode(), is(data2.hashCode()));
    }

    @Test
    void testToString() {

        RepeatingData data1 = RepeatingData.of(
                ArrayData.of("Apple", 5), ArrayData.of("Pear", 3));

        assertThat(data1.toString(), is("[{[1:f_1]=Apple, [2:f_2]=5}, {[1:f_1]=Pear, [2:f_2]=3}]"));

        RepeatingData data2 = RepeatingData.of(
                List.of(ArrayData.of("Apple", 5), ArrayData.of("Pear", 3)));

        assertThat(data2.toString(), is("[{[1:f_1]=Apple, [2:f_2]=5}, {[1:f_1]=Pear, [2:f_2]=3}]"));
    }
}
