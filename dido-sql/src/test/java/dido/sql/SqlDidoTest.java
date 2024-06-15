package dido.sql;

import dido.data.DidoData;
import dido.data.MapData;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SqlDidoTest {

    @Test
    void testSimpleInOddjob() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("SimpleSqlExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> results = lookup.lookup("results.beans", List.class);

        assertThat(results.get(0), is(MapData.of("TYPE", "Apple", "QUANTITY", 20)));
        assertThat(results.get(1), is(MapData.of("TYPE", "Orange", "QUANTITY", 30)));
        assertThat(results.get(2), is(MapData.of("TYPE", "Pear", "QUANTITY", 40)));
        assertThat(results.get(3), is(MapData.of("TYPE", "Grape", "QUANTITY", 55)));

        oddjob.destroy();
    }
}