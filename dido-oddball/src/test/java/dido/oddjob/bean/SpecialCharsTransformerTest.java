package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.MapData;
import dido.data.schema.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SpecialCharsTransformerTest {

    @Test
    public void testBuildWithReplacements() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Flavour (taste)", String.class)
                .addNamed("Weight (lbs.)", double.class)
                .addNamed("[Qty]", int.class)
                .addNamed("_Qty_", int.class)
                .build();

        Function<DidoData, DidoData> test = new SpecialCharsTransformer().toValue();

        DidoData data = MapData.builderForSchema(schema)
                .withString("Fruit", "Apple")
                .withString("Flavour (taste)", "Yummy")
                .withDouble("Weight (lbs.)", 22.2)
                .withInt("[Qty]", 2)
                .withInt("_Qty_", 3)
                .build();

        DidoData bean = test.apply(data);

        assertThat(bean.getStringNamed("Fruit"), is("Apple"));
        assertThat(bean.getStringNamed("Flavour _taste_"), is("Yummy"));
        assertThat(bean.getDoubleNamed("Weight _lbs__"), is(22.2));
        assertThat(bean.getIntNamed("_Qty_"), is(2));
        assertThat(bean.getIntNamed("_Qty__"), is(3));
    }

}