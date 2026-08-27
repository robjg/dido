package dido.oddjob.beanbus;

import dido.data.DataSchema;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class MapInOutBusTest {

    @Test
    void testInOddjob() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("MapInOut.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        String[] results = lookup.lookup("vars.results", String[].class);

        assertThat(results, arrayContaining(
                "The", "Quick", "Brown", "Fox", "Jumped", "Over", "The", "Lazy", "Dog"));

        DataSchema expected = DataSchema.builder()
                .addNamed("stuff", String.class)
                .build();

        assertThat(lookup.lookup("map-in.schema", DataSchema.class), is(expected));

        assertThat(lookup.lookup("map-in.count", int.class), is(9));
        assertThat(lookup.lookup("map-in.sent", int.class), is(9));

        assertThat(lookup.lookup("map-out.schema"), nullValue());

        assertThat(lookup.lookup("map-out.count", int.class), is(9));
        assertThat(lookup.lookup("map-out.sent", int.class), is(9));
    }

}
