package dido.data.immutable;

import dido.data.*;
import dido.data.NoSuchFieldException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ArrayDataTest {

    @Test
    void testEmptyData() {

        DidoData data = ArrayData.of();

        DataSchema schema = data.getSchema();

        assertThat(schema.firstIndex(), is(0));
        assertThat(schema.lastIndex(), is(0));
    }

    @Test
    void testSimpleData() {

        DidoData data = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data.getStringAt(1), is("Apple"));
        assertThat(data.hasAt(2), is(false));
        assertThat(data.getIntAt(3), is(15));
        assertThat(data.getDoubleAt(4), is(26.5));

        DataSchema schema = data.getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(4));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.nextIndex(4), is(0));

        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeAt(2), is(void.class));
        assertThat(schema.getTypeAt(3), is(Integer.class));
        assertThat(schema.getTypeAt(4), is(Double.class));

        assertThat(schema.getFieldNameAt(1), is("f_1"));
        assertThat(schema.getFieldNameAt(2), is("f_2"));
        assertThat(schema.getFieldNameAt(3), is("f_3"));
        assertThat(schema.getFieldNameAt(4), is("f_4"));
    }

    @Test
    void testFieldGetters() {

        DidoData data = ArrayData.of("Apple", null, 15, 26.5);

        ReadSchema readSchema = ReadSchema.from(data.getSchema());

        FieldGetter getter1 = readSchema.getFieldGetterAt(1);
        assertThat(getter1.get(data), is("Apple"));
        assertThat(getter1.getString(data), is("Apple"));

        FieldGetter getter2 = readSchema.getFieldGetterNamed("f_2");
        assertThat(getter2.get(data), Matchers.nullValue());
        assertThat(getter2.getString(data), is("null"));

        FieldGetter getter3 = readSchema.getFieldGetterAt(3);
        assertThat(getter3.get(data), is(15));
        assertThat(getter3.getInt(data), is(15));
        assertThat(getter3.getDouble(data), is(15.0));
        assertThat(getter3.getString(data), is("15"));

        FieldGetter getter4 = readSchema.getFieldGetterAt(4);
        assertThat(getter4.get(data), is(26.5));
        assertThat(getter4.getInt(data), is(26));
        assertThat(getter4.getDouble(data), is(26.5));
        assertThat(getter4.getString(data), is("26.5"));

        Assertions.assertThrows(dido.data.NoSuchFieldException.class,
                () -> readSchema.getFieldGetterAt(5));
        Assertions.assertThrows(dido.data.NoSuchFieldException.class,
                () -> readSchema.getFieldGetterAt(0));
        Assertions.assertThrows(NoSuchFieldException.class,
                () -> readSchema.getFieldGetterNamed("Wrong"));
    }

    @Test
    void testToString() {

        DidoData data1 = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data1.toString(), is("{[1:f_1]=Apple, [2:f_2]=null, [3:f_3]=15, [4:f_4]=26.5}"));
    }

    @Test
    void testEqualsAndHashCode() {

        DidoData data1 = ArrayData.of("Apple", null, 15, 26.5);

        DidoData data2 = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data1, is(data2));
        assertThat(data1.hashCode(), is(data2.hashCode()));

        DidoData data3 = ArrayData.of();

        assertThat(data1, not(is(data3)));
        assertThat(data3, not(is(data1)));

        DidoData data4 = ArrayData.withSchema(
                        DataSchema.builder()
                                .addAt(3, String.class)
                                .addAt(8, Void.class)
                                .addAt(11, int.class)
                                .addAt(19, double.class)
                                .build())
                .of("Apple", null, 15, 26.5);

        assertThat(data1, is(data4));
        assertThat(data1.hashCode(), is(data4.hashCode()));
    }

    @Test
    void testBuilderOf() {

        DidoData data1 = ArrayData.of("Apple", null, 15, 26.5);

        DidoData data2 = ArrayData.withSchema(data1.getSchema())
                .of("Apple", null, 15, 26.5);

        assertThat(data1, is(data2));
    }

    @Test
    void whenUsingBuilderThenSchemaDerived() {

        DidoData data1 = ArrayData.builder()
                .withString("Fruit", "Apple")
                .withString("Flavour", null)
                .withInt("Qty", 15)
                .withDouble("Price", 26.5)
                .build();

        DataSchema schema = data1.getSchema();

        assertThat(schema.getFieldNames(), contains("Fruit", "Flavour", "Qty", "Price"));

        assertThat(schema.getTypeAt(3), is(int.class));
        assertThat(schema.getTypeNamed("Price"), is(double.class));

        assertThat(DidoData.equals(data1, ArrayData.of("Apple", null, 15, 26.5)),
                is(true));

        DidoData data2 = ArrayData.builder()
                .copy(data1)
                .build();

        assertThat(data2, is(data1));
    }
}
