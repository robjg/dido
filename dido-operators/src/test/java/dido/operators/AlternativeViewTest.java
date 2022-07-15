package dido.operators;

import dido.data.*;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AlternativeViewTest {

    @Test
    void testSimpleFiledRename() {

        ArrayData.Builder<String> produceBuilder = ArrayData.builderForSchema(
                SchemaBuilder.forStringFields()
                        .addField("Type", String.class)
                        .addField("Quantity", int.class)
                        .addField("FarmId", int.class)
                        .build());

        GenericData<String> produce1 = produceBuilder.build("Apples", 12, 2);
        GenericData<String> produce2 = produceBuilder.build("Pears", 7, 1);

        Function<IndexedData<String>, GenericData<String>> mapper = AlternativeView.<String>with()
                .fieldChange("Type", "Fruit")
                .fieldChange("Quantity", "Qty")
                .make();

        GenericData<String> result1 = mapper.apply(produce1);

        DataSchema<String> schema1 = result1.getSchema();

        assertThat(schema1.firstIndex(), is(1));
        assertThat(schema1.getFieldAt(1), is("Fruit"));
        assertThat(schema1.getType("Fruit"), is(String.class));
        assertThat(schema1.getFieldAt(2), is("Qty"));
        assertThat(schema1.getIndex("Qty"), is(2));
        assertThat(schema1.lastIndex(), is(3));

        assertThat(schema1.getFields(), contains("Fruit", "Qty", "FarmId"));

        assertThat(result1.get("Fruit"), is("Apples"));
        assertThat(result1.getAt(1), is("Apples"));

        GenericData<String> result2 = mapper.apply(produce2);

        DataSchema<String> schema2 = result1.getSchema();

        assertThat(schema2, sameInstance(schema1));

        assertThat(result2.get("Fruit"), is("Pears"));

        assertThat(result2.toString(), is("{[1:Fruit]=Pears, [2:Qty]=7, [3:FarmId]=1}"));

        // Should this still work?

        assertThat(result1.get("Type"), is("Apples"));
    }
}