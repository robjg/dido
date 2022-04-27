package dido.oddjob.transpose;

import dido.data.DataSchema;
import dido.data.GenericData;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.state.ParentState;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataSetTest {

    @Test
    void testSimpleExample() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataSetExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<GenericData<String>> results = lookup.lookup("capture.beans", List.class);

        assertThat(results.size(), is(3));

        GenericData<String> result = results.get(0);

        DataSchema<String> schema = result.getSchema();

        assertThat(schema.getFieldAt(1), is("type"));
        assertThat(schema.getType("type"), is(String.class));
        assertThat(schema.getFieldAt(2), is("quantity"));
        assertThat(schema.getType("quantity"), is(int.class));
        assertThat(schema.getFieldAt(3), is("price"));
        assertThat(schema.getType("price"), is(double.class));

        assertThat(result.get("type"), is("Apple"));
        assertThat(result.getInt("quantity"), is(20));
        assertThat(result.getDouble("price"), is(27.2));

        oddjob.destroy();

    }
}