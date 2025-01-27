package dido.oddjob.util;

import dido.how.conversion.DidoConversionProvider;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Resettable;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.state.ParentState;

import javax.inject.Inject;
import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class DidoConverterJobTest {

    public static class NeedConverter implements Runnable {

        private DidoConversionProvider conversionProvider;

        @Override
        public void run() {

        }

        public DidoConversionProvider getConversionProvider() {
            return conversionProvider;
        }

        @Inject
        public void setConversionProvider(DidoConversionProvider conversionProvider) {
            this.conversionProvider = conversionProvider;
        }
    }

    @Test
    void testInOddjob() throws ArooaConversionException {

        File file = new File(Objects.requireNonNull(
                getClass().getResource("ConverterExample.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        DidoConversionProvider converter = lookup.lookup("foo.conversionProvider",
                DidoConversionProvider.class);

        assertThat(converter, notNullValue());

        DidoConversionProvider converter2 = lookup.lookup("converter.conversionProvider",
                DidoConversionProvider.class);

        assertThat(converter2, sameInstance(converter));

        lookup.lookup("converter", Resettable.class).hardReset();

        assertThat(lookup.lookup("converter.conversionProvider"), nullValue());
        assertThat(lookup.lookup("converter.services"), nullValue());
    }

}