package dido.oddjob.util;

import dido.how.conversion.DidoConverter;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.state.ParentState;

import javax.inject.Inject;
import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class DidoConverterJobTest {

    public static class NeedConverter implements Runnable{

        private DidoConverter converter;

        @Override
        public void run() {

        }

        public DidoConverter getConverter() {
            return converter;
        }

        @Inject
        public void setConverter(DidoConverter converter) {
            this.converter = converter;
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

        DidoConverter converter = new OddjobLookup(oddjob).lookup("foo.converter", DidoConverter.class);

        assertThat(converter, notNullValue());
    }
}