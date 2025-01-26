package dido.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CompactDatasTest {

    @Test
    void valuesAndToString() {

        DidoData data1 = ArrayData.of("Apple", null, 15, 26.5);

        CompactData copy1 = CompactDatas.extractorForIndices(
                        data1.getSchema(), 1, 2, 3, 4)
                .apply(data1);
        CompactData copy2 = CompactDatas.extractorForNames(
                        data1.getSchema(), "f_1", "f_2", "f_3", "f_4")
                .apply(data1);

        CompactSchema schema1 = copy1.getSchema();
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
        assertThat(copy1.hasAt(2), is(false));
        assertThat(copy1.getIntAt(3), is(15));
        assertThat(copy1.getDoubleAt(4), is(26.5));

        assertThat(copy1.toString(),
                is("{[1]=Apple, [2]=null, [3]=15, [4]=26.5}"));
        assertThat(copy2.toString(),
                is(copy1.toString()));

    }

    @Test
    void singleData() {

        DidoData data1 = ArrayData.of("Apple", 27, 15, 26.5);
        DidoData data2 = ArrayData.of(27, "Brown", "Orchard Farm");

        CompactData.Extractor extractor1 = CompactDatas.extractorForIndices(
                data1.getSchema(), 2);
        CompactData copy1 = extractor1.apply(data1);

        CompactData.Extractor extractor2 =
                CompactDatas.extractorForIndices(
                        data2.getSchema(), 1);
        CompactData copy2 = extractor2.apply(data2);

        CompactSchema schema1 = extractor1.getCompactSchema();
        assertThat(schema1.firstIndex(), is(1));
        assertThat(schema1.lastIndex(), is(1));
        assertThat(schema1.getTypeAt(1), is(Integer.class));

        assertThat(schema1.toString(), is("{[1]=java.lang.Integer}"));
        assertThat(extractor2.getCompactSchema(), is(schema1));

        assertThat(copy1, is(copy2));

        assertThat(copy1.toString(),
                is("{[1]=27}"));
        assertThat(copy2.toString(),
                is(copy1.toString()));

    }

    @Test
    void singleIntData() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Id", int.class)
                .build();

        DidoData data1 = ArrayData.valuesWithSchema(schema).of(42);
        DidoData data2 = ArrayData.valuesWithSchema(schema).of(42);

        CompactData.Extractor extractor1 = CompactDatas.extractorForIndices(
                data1.getSchema(), 1);
        CompactData copy1 = extractor1.apply(data1);

        CompactData.Extractor extractor2 =
                CompactDatas.extractorForIndices(
                        data2.getSchema(), 1);
        CompactData copy2 = extractor2.apply(data2);

        CompactSchema schema1 = extractor1.getCompactSchema();
        assertThat(schema1.firstIndex(), is(1));
        assertThat(schema1.lastIndex(), is(1));
        assertThat(schema1.getTypeAt(1), is(int.class));

        assertThat(schema1.toString(), is("{[1]=int}"));
        assertThat(extractor2.getCompactSchema(), is(schema1));

        assertThat(copy1, is(copy2));

        assertThat(copy1.toString(),
                is("{[1]=42}"));
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

        DidoData data1 = ArrayData.valuesWithSchema(schema)
                .of("A123",
                        List.of(ArrayData.valuesWithSchema(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesWithSchema(nestedSchema)
                                        .of("Pear", 5)));

        DidoData data2 = ArrayData.valuesWithSchema(schema)
                .of("A123",
                        List.of(ArrayData.valuesWithSchema(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesWithSchema(nestedSchema)
                                        .of("Pear", 5)));

        CompactData copy1 = CompactDatas.extractorForIndices(
                        data1.getSchema(), 1, 2)
                .apply(data1);
        CompactData copy2 = CompactDatas.extractorForNames(
                        data2.getSchema(), "OrderId", "OrderLines")
                .apply(data2);

        assertThat(copy1, is(copy2));
        assertThat(copy1.hashCode(), is(copy2.hashCode()));
    }

    @Test
    void partialIndices() {

        DidoData data1 = MapData.of(
                "Name", "Alice", "Number", 1234, "Colour", "Green");

        CompactData copy1 = CompactDatas.extractorForIndices(
                        data1.getSchema(), 1, 3)
                .apply(data1);
        CompactData copy2 = CompactDatas.extractorForNames(
                        data1.getSchema(), "Name", "Colour")
                .apply(data1);


        assertThat(copy1.getSchema().toString(), is("{[1]=java.lang.String, [2]=java.lang.String}"));
        assertThat(copy1.getSchema(), is(copy2.getSchema()));
        assertThat(copy1.getSchema().hashCode(), is(copy2.getSchema().hashCode()));

        assertThat(copy1.toString(), is("{[1]=Alice, [2]=Green}"));
        assertThat(copy2, is(copy1));
        assertThat(copy2.hashCode(), is(copy1.hashCode()));

        DidoData data2 = MapData.of(
                "Name", "Alice", "Number", 4567, "Colour", "Green");

        CompactData copy2_1 = CompactDatas.extractorForIndices(
                        data2.getSchema(), 1, 3)
                .apply(data2);

        assertThat(copy2_1.getSchema(), is(copy1.getSchema()));
        assertThat(copy2_1, is(copy1));
        assertThat(copy2_1.hashCode(), is(copy1.hashCode()));
    }

}