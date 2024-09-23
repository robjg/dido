package dido.data.generic;

import dido.data.DidoData;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class GenericDataTest {

    @Test
    void testEmptyDataToString() {

        assertThat(GenericData.toStringFieldsOnly(GenericData.emptyData(String.class)), is("{}"));
        assertThat(GenericData.toString(GenericData.emptyData(String.class)), is("{}"));
        MatcherAssert.assertThat(DidoData.toString(GenericData.emptyData(String.class)), is("{}"));
        assertThat(GenericData.emptyData(String.class).toString(), is("{}"));
    }

    @Test
    void testEmptyEqualsAndHashCode() {

        GenericData<String> emptyStringData = GenericData.emptyData(String.class);
        GenericData<Number> emptyNumberData = GenericData.emptyData(Number.class);

        assertThat(emptyNumberData.equals(emptyStringData), is(true));
        assertThat(emptyStringData.equals(emptyNumberData), is(true));

        assertThat(emptyStringData.hashCode(), is(0));
        assertThat(emptyNumberData.hashCode(), is(0));
    }
}