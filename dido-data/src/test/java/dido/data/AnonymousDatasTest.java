package dido.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AnonymousDatasTest {

    @Test
    void valuesAndToString() {

        DidoData data1 = ArrayData.of("Apple", null, 15, 26.5);

        AnonymousData copy1 = AnonymousDatas.copy(data1);
        AnonymousData copy2 = AnonymousDatas.wrap(data1);

        AnonymousSchema schema1 = copy1.getSchema();
        assertThat(schema1.firstIndex(), is(1));
        assertThat(schema1.lastIndex(), is(4));
        assertThat(schema1.nextIndex(1), is(2));
        assertThat(schema1.nextIndex(2), is(3));
        assertThat(schema1.nextIndex(3), is(4));
        assertThat(schema1.nextIndex(4), is(0));

        assertThat(schema1.toString(), is("{[1]=java.lang.String, [2]=void, [3]=java.lang.Integer, [4]=java.lang.Double}"));
        assertThat(copy2.getSchema(),
                is(schema1));
        assertThat(copy2.getSchema().hashCode(),
                is(schema1.hashCode()));
        assertThat(copy2.getSchema().toString(),
                is(schema1.toString()));

        assertThat(copy1.getStringAt(1), is("Apple"));
        assertThat(copy1.hasIndex(2), is(false));
        assertThat(copy1.getIntAt(3), is(15));
        assertThat(copy1.getDoubleAt(4), is(26.5));

        assertThat(copy1.toString(),
                is("{[1]=Apple, [2]=null, [3]=15, [4]=26.5}"));
        assertThat(copy2.toString(),
                is(copy1.toString()));

    }

    @Test
    void testEquals() {

        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", nestedSchema)
                .build();

        IndexedData data1 = ArrayData.valuesForSchema(schema)
                .of("A123",
                        List.of(ArrayData.valuesForSchema(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesForSchema(nestedSchema)
                                        .of("Pear", 5)));

        IndexedData data2 = ArrayData.valuesForSchema(schema)
                .of("A123",
                        List.of(ArrayData.valuesForSchema(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesForSchema(nestedSchema)
                                        .of("Pear", 5)));

        AnonymousData copy1 = AnonymousDatas.wrap(data1);
        AnonymousData copy2 = AnonymousDatas.copy(data1);

        assertThat(AnonymousData.equals(copy1, copy2), is(true));
        assertThat(copy1.hashCode(), is(copy2.hashCode()));
    }

    @Test
    void partialIndices() {

        DidoData data1 = MapData.of(
                "Name","Alice", "Number", 1234, "Colour", "Green");

        AnonymousData copy1 = AnonymousDatas.partialCopy(data1,1, 3);
        AnonymousData copy2 = AnonymousDatas.partialWrap(data1,1, 3);

        assertThat(copy1.getSchema().toString(), is("{[1]=java.lang.String, [2]=java.lang.String}"));
        assertThat(copy1.getSchema(), is(copy2.getSchema()));
        assertThat(copy1.getSchema().hashCode(), is(copy2.getSchema().hashCode()));

        assertThat(copy1.toString(), is("{[1]=Alice, [2]=Green}"));
        assertThat(copy2, is(copy1));
        assertThat(copy2.hashCode(), is(copy1.hashCode()));

        DidoData data2 = MapData.of(
                "Name","Alice", "Number", 4567, "Colour", "Green");

        AnonymousData copy2_1 = AnonymousDatas.partialWrap(data2, 1, 3);

        assertThat(copy2_1.getSchema(), is(copy1.getSchema()));
        assertThat(copy2_1, is(copy1));
        assertThat(copy2_1.hashCode(), is(copy1.hashCode()));
    }

}