package dido.operators.transform;

import dido.data.*;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TransformationFactoryTest {

    @Test
    void testSimpleFieldCopy() {

        ValueCopyFactory copy1 = new ValueCopyFactory();
        copy1.setField("fruit");
        copy1.setTo("food");

        TransformationFactory transformationFactory = new TransformationFactory();
        transformationFactory.setOf(0, copy1.get());
        transformationFactory.setWithCopy(true);

        Function<DidoData, DidoData> func = transformationFactory.get();

        NamedData data = MapData.of("fruit", "apple", "quantity", 12);

        DidoData result = func.apply(data);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("quantity", Integer.class)
                .addNamed("food", String.class)
                .build();

        assertThat(result.getSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesFor(expectedSchema).of("apple", 12, "apple");

        assertThat(result, is(expectedData));
    }

    @Test
    void testPartialFieldCopy() {

        ValueCopyFactory copy1 = new ValueCopyFactory();
        copy1.setField("fruit");
        copy1.setTo("food");

        TransformationFactory transformationFactory = new TransformationFactory();
        transformationFactory.setOf(0, copy1.get());
        transformationFactory.setWithCopy(false);

        Function<DidoData, DidoData> func = transformationFactory.get();

        DidoData data = MapData.of("fruit", "apple", "quantity", 12);

        DidoData result = func.apply(data);

        DataSchema schema = result.getSchema();

        assertThat(schema.getFieldNameAt(1), is("food"));
        assertThat(schema.getTypeNamed("food"), is(String.class));
        assertThat(schema.lastIndex(), is(1));

        assertThat(result.getNamed("food"), is("apple"));
        assertThat(result.getAt(1), is("apple"));
    }

    @Test
    void testCopyAll() {

        TransformationFactory transformationFactory = new TransformationFactory();
        transformationFactory.setWithCopy(true);

        Function<DidoData, DidoData> func = transformationFactory.get();

        DidoData data = MapData.of("fruit", "apple", "quantity", 12);

        DidoData result = func.apply(data);

        DataSchema schema = result.getSchema();

        assertThat(schema.getFieldNameAt(1), is("fruit"));
        assertThat(schema.getTypeNamed("fruit"), is(String.class));
        assertThat(schema.getFieldNameAt(2), is("quantity"));
        assertThat(schema.getTypeNamed("quantity"), is(Integer.class));

        assertThat(result.getNamed("fruit"), is("apple"));
        assertThat(result.getAt(1), is("apple"));
        assertThat(result.getIntNamed("quantity"), is(12));
        assertThat(result.getAt(2), is(12));
    }
}