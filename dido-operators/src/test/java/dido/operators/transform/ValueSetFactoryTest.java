package dido.operators.transform;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.SchemaBuilder;
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

class ValueSetFactoryTest {

    @Test
    void testSimpleExample() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataSetExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> results = lookup.lookup("capture.beans", List.class);

        assertThat(results.size(), is(3));

        DidoData result1 = results.get(0);

        DataSchema schema = result1.getSchema();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("type", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        assertThat(schema, is(expectedSchema));

        DidoData expectedData1 = ArrayData.valuesWithSchema(expectedSchema)
                        .of("Apple", 20, 27.2);

        assertThat(result1, is(expectedData1));

        DidoData result2 = results.get(1);

        DidoData expectedData2 = ArrayData.valuesWithSchema(expectedSchema)
                .of("Orange", 20, 31.6);

        assertThat(result2, is(expectedData2));

        oddjob.destroy();
    }
}