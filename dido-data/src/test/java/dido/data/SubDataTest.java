package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SubDataTest {

    @Test
    void testEqualsHashCodeAndToString() {

        GenericData<String> data1 = ArrayData.of("Alice", 1234, "Green");

        GenericData<String> subData1 = SubData.<String>ofIndices(1, 3).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.Object, [2]=java.lang.Object}"));
        assertThat(subData1.toString(), is("{[1]=Alice, [2]=Green}"));

        GenericData<String> data2 = ArrayData.of("Alice", 4567, "Green");

        GenericData<String> subData2 = SubData.<String>ofIndices(1, 3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testWithOneSubField() {

        GenericData<String> data1 = ArrayData.of("Alice", 1234, "Green");

        GenericData<String> subData1 = SubData.<String>ofIndices(1).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.Object}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        GenericData<String> data2 = ArrayData.of("Programmer", 47, "Alice");

        GenericData<String> subData2 = SubData.<String>ofIndices(3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testWithDifferentFieldNames() {

        GenericData<String> data1 = MapData.of("Name", "Alice",  "Id", 1234, "Colour", "Green");

        GenericData<String> subData1 = SubData.<String>ofIndices(1).apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        GenericData<String> data2 = MapData.of("Occupation", "Programmer", "Age", 47, "Called", "Alice");

        GenericData<String> subData2 = SubData.<String>ofIndices(3).apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }

    @Test
    void testSubDataWithFieldNames() {

        GenericData<String> data1 = MapData.of("Name", "Alice",  "Id", 1234, "Colour", "Green");

        GenericData<String> subData1 = SubData.ofFields("Name").apply(data1);

        assertThat(subData1.getSchema().toString(), is("{[1]=java.lang.String}"));
        assertThat(subData1.toString(), is("{[1]=Alice}"));

        GenericData<String> data2 = MapData.of("Occupation", "Programmer", "Age", 47, "Called", "Alice");

        GenericData<String> subData2 = SubData.ofFields("Called").apply(data2);

        assertThat(subData2.getSchema(), is(subData1.getSchema()));
        assertThat(subData2, is(subData1));
        assertThat(subData2.hashCode(), is(subData1.hashCode()));
    }
}