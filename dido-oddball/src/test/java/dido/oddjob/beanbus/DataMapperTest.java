package dido.oddjob.beanbus;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.logging.Appender;
import org.oddjob.arooa.logging.LoggerAdapter;
import org.oddjob.arooa.logging.LoggingEvent;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.logging.LogEnabled;
import org.oddjob.logging.OddjobNDC;
import org.oddjob.state.ParentState;
import org.oddjob.tools.StateSteps;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class DataMapperTest {

    private static class Messages implements Appender {

        final StringBuilder messages = new StringBuilder();

        final String filter;

        private Messages(String filter) {
            this.filter = filter;
        }

        @Override
        public void append(LoggingEvent event) {
            if (filter.equals(OddjobNDC.current()
                    .map(OddjobNDC.LogContext::getLogger)
                    .orElse(null))) {
                messages.append(event.getMessage());
            }
        }
    }

    @Test
    void testExceptionExample() throws ArooaPropertyException, ArooaConversionException {

        File config = new File(Objects.requireNonNull(
                getClass().getResource("BusMapExceptionExample.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(config);

        oddjob.load();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        LogEnabled mapLog = lookup.lookup("bus-map", LogEnabled.class);

        Messages messages = new Messages(mapLog.loggerName());


        LoggerAdapter.appenderAdapterFor((String) null)
                .addAppender(messages, LoggerAdapter.layoutFor("%p: %m%n"));

        StateSteps states = new StateSteps(oddjob);
        states.startCheck(ParentState.READY,
                ParentState.EXECUTING,
                ParentState.COMPLETE);

        oddjob.run();

        states.checkNow();

        List<?> results = lookup.lookup(
                "results.list", List.class);

        assertThat(results, Matchers.empty());

        assertThat(messages.messages.toString(),
                containsString("java.lang.IllegalArgumentException"));

        LoggerAdapter.appenderAdapterFor((String) null).removeAppender(messages);

        oddjob.destroy();
    }

}