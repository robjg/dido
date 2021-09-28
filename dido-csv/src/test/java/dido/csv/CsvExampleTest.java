package dido.csv;

import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CsvExampleTest {

    @Test
    void testReadInOddjobAndTransformToBeans() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("FromCsvExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<Fruit> results = lookup.lookup("capture.beans", List.class);

        assertThat(results.get(0).getType(), is("Apple"));
        assertThat(results.get(1).getType(), is("Orange"));
        assertThat(results.get(2).getType(), is("Pear"));

    }

    @Test
    void testReadAndWriteNoSchema() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("FromToCsvExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        String[] results = lookup.lookup("results", String[].class);

        assertThat(results[0], is("Fruit,Qty,Price"));
        assertThat(results[1], is("Apple,5,27.2"));
        assertThat(results[2], is("Orange,10,31.6"));
        assertThat(results[3], is("Pear,7,22.1"));
    }
}