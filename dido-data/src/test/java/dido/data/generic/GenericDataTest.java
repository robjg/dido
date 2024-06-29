package dido.data.generic;

import dido.data.DidoData;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class GenericDataTest {

    @Test
    void testEmptyDataToString() {

        assertThat(GenericData.toStringFieldsOnly(GenericData.emptyData()), is("{}"));
        assertThat(GenericData.toString(GenericData.emptyData()), is("{}"));
        MatcherAssert.assertThat(DidoData.toString(GenericData.emptyData()), is("{}"));
        assertThat(GenericData.emptyData().toString(), is("{}"));
    }

    @Test
    void testEmptyEqualsAndHashCode() {

        GenericData<String> emptyStringData = GenericData.emptyData();
        GenericData<Number> emptyNumberData = GenericData.emptyData();

        assertThat(emptyNumberData.equals(emptyStringData), is(true));
        assertThat(emptyStringData.equals(emptyNumberData), is(true));

        assertThat(emptyStringData.hashCode(), is(0));
        assertThat(emptyNumberData.hashCode(), is(0));
    }
}