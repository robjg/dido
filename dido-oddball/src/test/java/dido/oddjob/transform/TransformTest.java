package dido.oddjob.transform;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapData;
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
        copy1.setArooaSession(session);
        copy1.setField("fruit");
        copy1.setTo("food");

        Transform<String, String> transform = new Transform<>();
        transform.setOf(0, copy1.toValue());

        Function<GenericData<String>, GenericData<String>> func = transform.toValue();

        GenericData<String> data = MapData.of("fruit", "apple", "quantity", 12);

        GenericData<String> result = func.apply(data);

        DataSchema<String> schema = result.getSchema();

        assertThat(schema.getFieldAt(1), is("food"));
        assertThat(schema.getType("food"), is(String.class));
        assertThat(schema.getFieldAt(2), is("quantity"));
        assertThat(schema.getType("quantity"), is(Integer.class));

        assertThat(result.get("food"), is("apple"));
        assertThat(result.getAt(1), is("apple"));
        assertThat(result.getInt("quantity"), is(12));
        assertThat(result.getAt(2), is(12));
    }

    @Test
    void testPartialFieldCopy() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory copy1 = new ValueCopyFactory();
        copy1.setArooaSession(session);
        copy1.setField("fruit");
        copy1.setTo("food");

        Transform<String, String> transform = new Transform<>();
        transform.setOf(0, copy1.toValue());
        transform.setStrategy(SchemaStrategy.NEW);

        Function<GenericData<String>, GenericData<String>> func = transform.toValue();

        GenericData<String> data = MapData.of("fruit", "apple", "quantity", 12);

        GenericData<String> result = func.apply(data);

        DataSchema<String> schema = result.getSchema();

        assertThat(schema.getFieldAt(1), is("food"));
        assertThat(schema.getType("food"), is(String.class));
        assertThat(schema.lastIndex(), is(1));

        assertThat(result.get("food"), is("apple"));
        assertThat(result.getAt(1), is("apple"));
    }

    @Test
    void testCopyAll() {

        Transform<String, String> transform = new Transform<>();

        Function<GenericData<String>, GenericData<String>> func = transform.toValue();

        GenericData<String> data = MapData.of("fruit", "apple", "quantity", 12);

        GenericData<String> result = func.apply(data);

        DataSchema<String> schema = result.getSchema();

        assertThat(schema.getFieldAt(1), is("fruit"));
        assertThat(schema.getType("fruit"), is(String.class));
        assertThat(schema.getFieldAt(2), is("quantity"));
        assertThat(schema.getType("quantity"), is(Integer.class));

        assertThat(result.get("fruit"), is("apple"));
        assertThat(result.getAt(1), is("apple"));
        assertThat(result.getInt("quantity"), is(12));
        assertThat(result.getAt(2), is(12));
    }
}