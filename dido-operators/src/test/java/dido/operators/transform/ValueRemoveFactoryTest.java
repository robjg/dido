package dido.operators.transform;

import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.state.ParentState;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ValueRemoveFactoryTest {

    @Test
    void simpleExample() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("ValueRemoveExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        oddjob.destroy();
    }
}