package dido.operators;

import dido.data.*;
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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class FlattenTypeTest {

    @Test
    void testExample() throws ArooaConversionException, InterruptedException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("FlattenExample.xml")).getFile()));

        StateSteps state = new StateSteps(oddjob);
        state.startCheck(ParentState.READY, ParentState.EXECUTING,
                 ParentState.COMPLETE);

        oddjob.run();

        state.checkWait();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<GenericData<String>> results = lookup.lookup("results.beans", List.class);

        DataSchema<String> expectedSchema = SchemaBuilder.forStringFields()
                .addField("OrderId", String.class)
                .addField("Fruit", String.class)
                .addField("Qty", int.class)
                .build();

        IndexedData<String> expected1 = ArrayData.valuesFor(expectedSchema)
                .of("A123", "Apple", 4);
        IndexedData<String> expected2 = ArrayData.valuesFor(expectedSchema)
                .of("A123", "Pear", 5);

        assertThat(results, contains(expected1, expected2));
    }

    @Test
    void testColumnsExample() throws ArooaConversionException, InterruptedException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("FlattenColumnsExample.xml")).getFile()));

        StateSteps state = new StateSteps(oddjob);
        state.startCheck(ParentState.READY, ParentState.EXECUTING,
                ParentState.COMPLETE);

        oddjob.run();

        state.checkWait();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<GenericData<String>> results = lookup.lookup("results.beans", List.class);

        DataSchema<String> expectedSchema = SchemaBuilder.forStringFields()
                .addField("Name", String.class)
                .addField("Numbers", Integer.class)
                .addField("Letters", Object.class)
                .build();

        // Todo Specify Schema

        assertThat(results.get(0).getSchema(), is(expectedSchema));

        IndexedData<String> expected1 = ArrayData.valuesFor(expectedSchema)
                .of("Foo", 1, "X");
        IndexedData<String> expected2 = ArrayData.valuesFor(expectedSchema)
                .of("Foo", 2, "Y");
        IndexedData<String> expected3 = ArrayData.valuesFor(expectedSchema)
                .of("Foo", 3, null);

        assertThat(results, contains(expected1, expected2, expected3));
    }
}