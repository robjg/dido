package dido.operators;

import dido.data.ArrayData;
import dido.data.GenericData;
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
        List<GenericData<String>> results = lookup.lookup("results.beans", List.class);

        ArrayData.Builder<String> expectedBuilder = ArrayData.builderForSchema(
                SchemaBuilder.forStringFields()
                        .addField("Type", String.class)
                        .addField("Quantity", int.class)
                        .addField("FarmId", int.class)
                        .addField("Id", int.class)
                        .addField("Farmer", String.class)
                        .build());

        GenericData<String> expected1 = expectedBuilder.build("Apples", 12, 2, 2, "Giles");
        GenericData<String> expected2 = expectedBuilder.build("Pears", 7, 1, 1, "Brown");
        GenericData<String> expected3 = expectedBuilder.build("Carrots", 15, 2, 2, "Giles");

        assertThat(results, containsInAnyOrder(expected1, expected2, expected3));
    }

    @Test
    void testExample2() throws ArooaConversionException, InterruptedException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("LeftJoinExample2.xml")).getFile()));

        StateSteps state = new StateSteps(oddjob);
        state.startCheck(ParentState.READY, ParentState.EXECUTING,
                ParentState.ACTIVE, ParentState.STARTED, ParentState.COMPLETE);

        oddjob.run();

        state.checkWait();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<GenericData<String>> results = lookup.lookup("results.beans", List.class);

        ArrayData.Builder<String> expectedBuilder = ArrayData.builderForSchema(
                SchemaBuilder.forStringFields()
                        .addField("Type", String.class)
                        .addField("Quantity", int.class)
                        .addField("FarmId", int.class)
                        .addField("Id", int.class)
                        .addField("Farmer", String.class)
                        .build());

        GenericData<String> expected1 = expectedBuilder.build("Apples", 12, 2, 2, "Giles");
        GenericData<String> expected2 = expectedBuilder.build("Pears", 7, 1, 1, "Brown");
        GenericData<String> expected3 = expectedBuilder.build("Carrots", 15, 2, 2, "Giles");

        assertThat(results, containsInAnyOrder(expected1, expected2, expected3));
    }

    @Test
    void testMultiKeyExample() throws ArooaConversionException, InterruptedException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("LeftJoinMultiKeyExample.xml")).getFile()));

        StateSteps state = new StateSteps(oddjob);
        state.startCheck(ParentState.READY, ParentState.EXECUTING,
                ParentState.ACTIVE, ParentState.STARTED, ParentState.COMPLETE);

        oddjob.run();

        state.checkWait();

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<GenericData<String>> results = lookup.lookup("results.beans", List.class);

        ArrayData.Builder<String> expectedBuilder = ArrayData.builderForSchema(
                SchemaBuilder.forStringFields()
                        .addField("Type", String.class)
                        .addField("Variety", String.class)
                        .addField("Quantity", int.class)
                        .addField("FarmId", int.class)
                        .addField("Country", String.class)
                        .addField("Id", int.class)
                        .addField("Farmer", String.class)
                        .build());

        GenericData<String> expected1 = expectedBuilder.build("Apples", "Cox", 12, 2, "UK", 2, "Giles");
        GenericData<String> expected2 = expectedBuilder.build("Pears", "Conference", 7, 1, "FR", 1, "Brun");
        GenericData<String> expected3 = expectedBuilder.build("Carrots", "", 15, 2, "UK", 2, "Giles");

        assertThat(results, containsInAnyOrder(expected1, expected2, expected3));
    }
}