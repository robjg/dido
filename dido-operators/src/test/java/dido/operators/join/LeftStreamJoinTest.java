package dido.operators.join;

import dido.data.ArrayData;
import dido.data.DidoData;
import dido.data.FromValues;
import dido.data.schema.SchemaBuilder;
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

        FromValues farmBuilder = ArrayData.withSchema(
                SchemaBuilder.newInstance()
                        .addNamed("Id", int.class)
                        .addNamed("Farmer", String.class)
                        .build());

        DidoData farm1 = farmBuilder.of(1, "Brown");
        DidoData farm2 = farmBuilder.of(2, "Giles");

        FromValues produceBuilder = ArrayData.withSchema(
                SchemaBuilder.newInstance()
                        .addNamed("Type", String.class)
                        .addNamed("Quantity", int.class)
                        .addNamed("FarmId", int.class)
                        .build());

        DidoData produce1 = produceBuilder.of("Apples", 12, 2);
        DidoData produce2 = produceBuilder.of("Pears", 7, 1);
        DidoData produce3 = produceBuilder.of("Carrots", 15, 2);

        FromValues expectedBuilder = ArrayData.withSchema(
                SchemaBuilder.newInstance()
                        .addNamed("Type", String.class)
                        .addNamed("Quantity", int.class)
                        .addNamed("FarmId", int.class)
                        .addNamed("Id", int.class)
                        .addNamed("Farmer", String.class)
                        .build());

        DidoData expected1 = expectedBuilder.of("Apples", 12, 2, 2, "Giles");
        DidoData expected2 = expectedBuilder.of("Pears", 7, 1, 1, "Brown");
        DidoData expected3 = expectedBuilder.of("Carrots", 15, 2, 2, "Giles");
        DidoData expected4 = expectedBuilder.of("Apples", 3, 2, 2, "Giles");

        List<DidoData> results = new ArrayList<>(3);

        StreamJoin join = LeftStreamJoin.<String>with()
                .primaryIndices(1)
                .secondaryIndices(1)
                .foreignIndices(3)
                .make();

        Consumer<DidoData> primary = join.getPrimary();
        Consumer<DidoData> secondary = join.getSecondary();
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

        primary.accept(ArrayData.builderForSchema(produce1.getSchema())
                .copy(produce1)
                .withInt("Quantity", 3)
                .build());

        assertThat(results, contains(expected1, expected2, expected3, expected4));

    }
}