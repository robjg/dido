package dido.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RepeatingDataTest {

    @Test
    void testEqualsAndHashCode() {

        RepeatingData<String> data1 = RepeatingData.of(
                ArrayData.of("Apple", 5), ArrayData.of("Pear", 3));

        RepeatingData<String> data2 = RepeatingData.of(
                List.of(ArrayData.of("Apple", 5), ArrayData.of("Pear", 3)));

        assertThat(data1, is(data2));
        assertThat(data1.hashCode(), is(data2.hashCode()));
    }

    @Test
    void testToString() {

        RepeatingData<String> data1 = RepeatingData.of(
                ArrayData.of("Apple", 5), ArrayData.of("Pear", 3));

        assertThat(data1.toString(), is("[[Apple, 5], [Pear, 3]]"));

        RepeatingData<String> data2 = RepeatingData.of(
                List.of(ArrayData.of("Apple", 5), ArrayData.of("Pear", 3)));

        assertThat(data2.toString(), is("[[Apple, 5], [Pear, 3]]"));
    }
}
