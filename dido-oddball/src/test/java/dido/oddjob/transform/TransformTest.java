package dido.oddjob.transform;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
import dido.data.NamedData;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TransformTest {

    @Test
    void testSimpleFieldCopy() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory copy1 = new ValueCopyFactory();
        copy1.setField("fruit");
        copy1.setTo("food");

        Transform transform = new Transform();
        transform.setOf(0, copy1.get());

        Function<DidoData, DidoData> func = transform.toValue();

        NamedData data = MapData.of("fruit", "apple", "quantity", 12);

        DidoData result = func.apply(data);

        DataSchema schema = result.getSchema();

        assertThat(schema.getFieldNameAt(1), is("food"));
        assertThat(schema.getTypeNamed("food"), is(String.class));
        assertThat(schema.getFieldNameAt(2), is("quantity"));
        assertThat(schema.getTypeNamed("quantity"), is(Integer.class));

        assertThat(result.getNamed("food"), is("apple"));
        assertThat(result.getAt(1), is("apple"));
        assertThat(result.getIntNamed("quantity"), is(12));
        assertThat(result.getAt(2), is(12));
    }

    @Test
    void testPartialFieldCopy() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory copy1 = new ValueCopyFactory();
        copy1.setField("fruit");
        copy1.setTo("food");

        Transform transform = new Transform();
        transform.setOf(0, copy1.get());
        transform.setStrategy(SchemaStrategy.NEW);

        Function<DidoData, DidoData> func = transform.toValue();

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

        Transform transform = new Transform();

        Function<DidoData, DidoData> func = transform.toValue();

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