package dido.operators.join;

import dido.data.ArrayData;
import dido.data.DidoData;
import dido.data.FromValues;
import dido.data.SchemaBuilder;
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
import static org.hamcrest.Matchers.containsInAnyOrder;

class StreamJoinServiceTest {

    @Test
    void testExample() throws ArooaConversionException, InterruptedException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("LeftJoinExample.xml")).getFile()));

        StateSteps state = new StateSteps(oddjob);
        state.startCheck(ParentState.READY, ParentState.EXECUTING,
                ParentState.ACTIVE, ParentState.STARTED, ParentState.COMPLETE);

        oddjob.run();

        state.checkWait();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> results = lookup.lookup("results.beans", List.class);

        FromValues expectedValues = ArrayData.withSchema(
                SchemaBuilder.newInstance()
                        .addNamed("Type", String.class)
                        .addNamed("Quantity", int.class)
                        .addNamed("FarmId", int.class)
                        .addNamed("Id", int.class)
                        .addNamed("Farmer", String.class)
                        .build());

        DidoData expected1 = expectedValues.of("Apples", 12, 2, 2, "Giles");
        DidoData expected2 = expectedValues.of("Pears", 7, 1, 1, "Brown");
        DidoData expected3 = expectedValues.of("Carrots", 15, 2, 2, "Giles");

        assertThat(results, containsInAnyOrder(expected1, expected2, expected3));
    }

    @Test
    void testExample2() throws ArooaConversionException, InterruptedException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("LeftJoinExample2.xml")).getFile()));

        StateSteps state = new StateSteps(oddjob);
        state.startCheck(
                StateSteps.definitely(ParentState.READY),
                StateSteps.definitely(ParentState.EXECUTING),
                StateSteps.maybe(ParentState.ACTIVE),
                StateSteps.definitely(ParentState.STARTED),
                StateSteps.definitely(ParentState.COMPLETE));

        oddjob.run();

        state.checkWait();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> results = lookup.lookup("results.beans", List.class);

        FromValues values = ArrayData.withSchema(
                SchemaBuilder.newInstance()
                        .addNamed("Type", String.class)
                        .addNamed("Quantity", int.class)
                        .addNamed("FarmId", int.class)
                        .addNamed("Id", int.class)
                        .addNamed("Farmer", String.class)
                        .build());

        DidoData expected1 = values.of("Apples", 12, 2, 2, "Giles");
        DidoData expected2 = values.of("Pears", 7, 1, 1, "Brown");
        DidoData expected3 = values.of("Carrots", 15, 2, 2, "Giles");

        assertThat(results, containsInAnyOrder(expected1, expected2, expected3));
    }

    @Test
    void testMultiKeyExample() throws ArooaConversionException, InterruptedException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("LeftJoinMultiKeyExample.xml")).getFile()));

        StateSteps state = new StateSteps(oddjob);
        state.startCheck(
                StateSteps.definitely(ParentState.READY),
                StateSteps.definitely(ParentState.EXECUTING),
                StateSteps.maybe(ParentState.ACTIVE),
                StateSteps.definitely(ParentState.STARTED),
                StateSteps.definitely(ParentState.COMPLETE));

        oddjob.run();

        state.checkWait();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> results = lookup.lookup("results.beans", List.class);

        FromValues values = ArrayData.withSchema(
                SchemaBuilder.newInstance()
                        .addNamed("Type", String.class)
                        .addNamed("Variety", String.class)
                        .addNamed("Quantity", int.class)
                        .addNamed("FarmId", int.class)
                        .addNamed("Country", String.class)
                        .addNamed("Id", int.class)
                        .addNamed("Farmer", String.class)
                        .build());

        DidoData expected1 = values.of("Apples", "Cox", 12, 2, "UK", 2, "Giles");
        DidoData expected2 = values.of("Pears", "Conference", 7, 1, "FR", 1, "Brun");
        DidoData expected3 = values.of("Carrots", "", 15, 2, "UK", 2, "Giles");

        assertThat(results, containsInAnyOrder(expected1, expected2, expected3));
    }
}