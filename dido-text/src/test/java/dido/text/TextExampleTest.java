package dido.text;

import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TextExampleTest {

    private static final Logger logger = LoggerFactory.getLogger(TextExampleTest.class);

    @Test
    void testWriteTextFiles() throws ArooaConversionException {

        logger.info("Logger is " + logger.getClass().getName());

        String expected =
                "Fruit     |Quantity| Price" + System.lineSeparator() +
                "----------+--------+------" + System.lineSeparator() +
                "Apple     |       5|  22.3" + System.lineSeparator() +
                "Cantaloupe|      27| 245.3" + System.lineSeparator() +
                "Pear      |     232|11.328" + System.lineSeparator();

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("/config/ToTableExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        String results = lookup.lookup("results", String.class);

        assertThat(results, is(expected));
    }
}
