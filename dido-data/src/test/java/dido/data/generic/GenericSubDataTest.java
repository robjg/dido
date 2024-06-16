package dido.data.generic;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class GenericSubDataTest {

    @Test
    void testEqualsHashCodeAndToString() {

        GenericData<String> data1 = GenericMapData.of(
                "Name","Alice", "Number", 1234, "Colour", "Green");

        GenericData<String> subData1 = GenericSubData.<String>ofIndices(1, 3).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String, [2]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice, [2]=Green}"));

        GenericData<String> data2 = GenericMapData.of(
                        "Name","Alice", "Number", 4567, "Colour", "Green");

        GenericData<String> subData2 = GenericSubData.<String>ofIndices(1, 3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testWithOneSubField() {

        GenericData<String> data1 = GenericMapData.of(
                "Name","Alice", "Number", 1234, "Colour", "Green");

        GenericData<String> subData1 = GenericSubData.<String>ofIndices(1).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        GenericData<String> data2 = GenericMapData.of(
                "Job", "Programmer", "Age", 47, "Name", "Alice");

        GenericData<String> subData2 = GenericSubData.<String>ofIndices(3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testWithDifferentFieldNames() {

        GenericData<String> data1 = GenericMapData.of(
                "Name", "Alice",  "Id", 1234, "Colour", "Green");

        GenericData<String> subData1 = GenericSubData.<String>ofIndices(1).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        GenericData<String> data2 = GenericMapData.of(
                "Occupation", "Programmer", "Age", 47, "Called", "Alice");

        GenericData<String> subData2 = GenericSubData.<String>ofIndices(3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testSubDataWithFieldNames() {

        GenericData<String> data1 = GenericMapData.of(
                "Name", "Alice",  "Id", 1234, "Colour", "Green");

        GenericData<String> subData1 = GenericSubData.ofFields("Name").apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        GenericData<String> data2 = GenericMapData.of(
                "Occupation", "Programmer", "Age", 47, "Called", "Alice");

        GenericData<String> subData2 = GenericSubData.ofFields("Called").apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }
}