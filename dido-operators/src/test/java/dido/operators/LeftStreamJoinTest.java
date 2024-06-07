package dido.operators;

import dido.data.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

class LeftStreamJoinTest {

    @Test
    void testSimpleExample() {

        ArrayData.Builder farmBuilder = ArrayData.builderForSchema(
                SchemaBuilder.forStringFields()
                        .addField("Id", int.class)
                        .addField("Farmer", String.class)
                        .build());

        GenericData<String> farm1 = farmBuilder.build(1, "Brown");
        GenericData<String> farm2 = farmBuilder.build(2, "Giles");

        ArrayData.Builder produceBuilder = ArrayData.builderForSchema(
                SchemaBuilder.forStringFields()
                        .addField("Type", String.class)
                        .addField("Quantity", int.class)
                        .addField("FarmId", int.class)
                        .build());

        GenericData<String> produce1 = produceBuilder.build("Apples", 12, 2);
        GenericData<String> produce2 = produceBuilder.build("Pears", 7, 1);
        GenericData<String> produce3 = produceBuilder.build("Carrots", 15, 2);

        ArrayData.Builder expectedBuilder = ArrayData.builderForSchema(
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

        List<GenericData<String>> results = new ArrayList<>(3);

        StreamJoin<String> join = LeftStreamJoin.<String>with()
                .primaryIndices(1)
                .secondaryIndices(1)
                .foreignIndices(3)
                .make();

        Consumer<IndexedData<String>> primary = join.getPrimary();
        Consumer<IndexedData<String>> secondary = join.getSecondary();
        join.setTo(results::add);

        primary.accept(produce1);

        assertThat(results, empty());

        secondary.accept(farm1);

        assertThat(results, empty());

        secondary.accept(farm2);

        assertThat(results, contains(expected1));

        primary.accept(produce2);

        assertThat(results, contains(expected1, expected2));
        assertThat(expected1.getSchema(), Matchers.is(expected2.getSchema()));

        primary.accept(produce3);

        assertThat(results, contains(expected1, expected2, expected3));
        assertThat(expected2.getSchema(), Matchers.is(expected3.getSchema()));

        MapData.copy(produce1).setInt("Quantity", 7)
                .to(primary);

    }
}