package dido.proto;


import dido.data.DidoData;
import dido.data.MapData;
import dido.foo.Person;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.state.ParentState;
import org.oddjob.tools.StateSteps;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ProtoToBeanTypeTest {

    @Test
    void createBeanFromProto() {

        DidoData data = MapData.of(
                "name", "Alice",
                "id", 234,
                "email", "alice@foo.com");

        byte[] bytes = ToProtoBytes.from(Person.getDescriptor()).apply(data);

        ProtoToBeanType<Person> test = new ProtoToBeanType<>();
        test.setProtoClass(Person.class);

        Person person = test.get().apply(bytes);

        assertThat(person.getName(), is("Alice"));
    }

    @Test
    void example() throws InterruptedException, ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("ProtoToBeanExample.xml")).getFile()));

        StateSteps state = new StateSteps(oddjob);
        state.startCheck(ParentState.READY, ParentState.EXECUTING, ParentState.COMPLETE);

        oddjob.run();

        state.checkNow();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<Person> results = lookup.lookup("results.beans", List.class);

        Person person = results.get(0);

        assertThat(person.getName(), is("Alice"));
    }
}