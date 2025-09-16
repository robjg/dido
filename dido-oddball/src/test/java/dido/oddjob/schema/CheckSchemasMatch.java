package dido.oddjob.schema;

import dido.data.DataSchema;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CheckSchemasMatch implements Runnable {

    private DataSchema actual;

    private DataSchema expected;

    @Override
    public void run() {
        assertThat(actual, is(expected));
    }

    public DataSchema getActual() {
        return actual;
    }

    public void setActual(DataSchema actual) {
        this.actual = actual;
    }

    public DataSchema getExpected() {
        return expected;
    }

    public void setExpected(DataSchema expected) {
        this.expected = expected;
    }
}
