package dido.replay;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.test.OurDirs;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class OddjobRecordReplayTest {

    @Test
    void simpleExample() throws ArooaConversionException, IOException {

        Path workDir = OurDirs.workPathDir(OddjobRecordReplayTest.class);

        Properties properties = new Properties();
        properties.setProperty("work.dir", workDir.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setProperties(properties);
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("RecordPlayExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> results = lookup.lookup("play.to", List.class);

        DataSchema schema = results.get(0).getSchema();
        assertThat(schema.getTypeNamed("type"), is(String.class));
        assertThat(schema.getTypeNamed("quantity"), is(int.class));
        assertThat(schema.getTypeNamed("price"), is(double.class));

        DidoData data1 = results.get(0);

        assertThat(data1.get("type"), is("Apple"));
        assertThat(data1.get("quantity"), is(5));
        assertThat(data1.get("price"), is(27.2));

        assertThat(results.get(1).get("type"), is("Orange"));
        assertThat(results.get(2).get("type"), is("Pear"));
    }
}
