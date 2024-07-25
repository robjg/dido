package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class SubDataTest {

    @Test
    void testEqualsHashCodeAndToString() {

        NamedData data1 = MapData.of(
                "Name","Alice", "Number", 1234, "Colour", "Green");

        DidoData subData1 = SubData.ofIndices(1, 3).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1:Name]=java.lang.String, [2:Colour]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1:Name]=Alice, [2:Colour]=Green}"));

        NamedData data2 = MapData.of(
                        "Name","Alice", "Number", 4567, "Colour", "Green");

        DidoData subData2 = SubData.ofIndices(1, 3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testWithOneSubField() {

        NamedData data1 = MapData.of(
                "Name","Alice", "Number", 1234, "Colour", "Green");

        DidoData subData1 = SubData.ofIndices(1).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1:Name]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1:Name]=Alice}"));

        DidoData data2 = MapData.of(
                "Job", "Programmer", "Age", 47, "Name", "Alice");

        DidoData subData2 = SubData.ofIndices(3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testWithDifferentFieldNames() {

        NamedData data1 = MapData.of(
                "Name", "Alice",  "Id", 1234, "Colour", "Green");

        DidoData subData1 = SubData.ofIndices(1).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1:Name]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1:Name]=Alice}"));

        NamedData data2 = MapData.of(
                "Occupation", "Programmer", "Age", 47, "Called", "Alice");

        DidoData subData2 = SubData.ofIndices(3).apply(data2);

        // Since field names now mandatory, these are not equal
        assertThat(subData2.getSchema(), not(is(subData1.getSchema())));
        assertThat(subData2, not(is(subData1)));
        // Hash code currently based on index so these are equal!
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testSubDataWithFieldNames() {

        NamedData data1 = MapData.of(
                "Name", "Alice",  "Id", 1234, "Colour", "Green");

        DidoData subData1 = SubData.with().fields().andFields("Name").apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1:Name]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1:Name]=Alice}"));

        DidoData data2 = MapData.of(
                "Occupation", "Programmer", "Age", 47, "Called", "Alice");

        DidoData subData2 = SubData.ofFields("Called").apply(data2);

        // Since field names now mandatory, these are not equal
        assertThat(subData2.getSchema(), not(is(subData1.getSchema())));
        assertThat(subData2, not(is(subData1)));
        // Hash code currently based on index so these are equal!
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }
}