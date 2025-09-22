package dido.operators;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.data.schema.SchemaBuilder;
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
        List<DidoData> results = lookup.lookup("results.beans", List.class);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .build();

        DidoData expected1 = ArrayData.withSchema(expectedSchema)
                .of("A123", "Apple", 4);
        DidoData expected2 = ArrayData.withSchema(expectedSchema)
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
        List<DidoData> results = lookup.lookup("results.beans", List.class);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("Name", String.class)
                .addNamed("Numbers", Integer.class)
                .addNamed("Letters", String.class)
                .build();

        // Todo Specify Schema

        assertThat(results.get(0).getSchema(), is(expectedSchema));

        DidoData expected1 = ArrayData.withSchema(expectedSchema)
                .of("Foo", 1, "X");
        DidoData expected2 = ArrayData.withSchema(expectedSchema)
                .of("Foo", 2, "Y");
        DidoData expected3 = ArrayData.withSchema(expectedSchema)
                .of("Foo", 3, null);

        assertThat(results, contains(expected1, expected2, expected3));
    }
}