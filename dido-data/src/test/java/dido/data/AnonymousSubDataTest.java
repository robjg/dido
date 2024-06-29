package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AnonymousSubDataTest {

    @Test
    void testEqualsHashCodeAndToString() {

        NamedData data1 = MapData.of(
                "Name","Alice", "Number", 1234, "Colour", "Green");

        AnonymousData subData1 = AnonymousSubData.ofIndices(1, 3).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String, [2]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice, [2]=Green}"));

        NamedData data2 = MapData.of(
                        "Name","Alice", "Number", 4567, "Colour", "Green");

        AnonymousData subData2 = AnonymousSubData.ofIndices(1, 3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testWithOneSubField() {

        NamedData data1 = MapData.of(
                "Name","Alice", "Number", 1234, "Colour", "Green");

        AnonymousData subData1 = AnonymousSubData.<String>ofIndices(1).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        NamedData data2 = MapData.of(
                "Job", "Programmer", "Age", 47, "Name", "Alice");

        AnonymousData subData2 = AnonymousSubData.ofIndices(3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testWithDifferentFieldNames() {

        NamedData data1 = MapData.of(
                "Name", "Alice",  "Id", 1234, "Colour", "Green");

        AnonymousData subData1 = AnonymousSubData.ofIndices(1).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        NamedData data2 = MapData.of(
                "Occupation", "Programmer", "Age", 47, "Called", "Alice");

        AnonymousData subData2 = AnonymousSubData.ofIndices(3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testSubDataWithFieldNames() {

        NamedData data1 = MapData.of(
                "Name", "Alice",  "Id", 1234, "Colour", "Green");

        AnonymousData subData1 = AnonymousSubData.ofFields("Name").apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        NamedData data2 = MapData.of(
                "Occupation", "Programmer", "Age", 47, "Called", "Alice");

        AnonymousData subData2 = AnonymousSubData.ofFields("Called").apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }
}