package dido.oddjob.bean;

import dido.data.*;
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

        Function<DidoData, NamedData> test = new SpecialCharsTransformer().toValue();

        DidoData data = MapData.newBuilder(schema)
                .withString("Fruit", "Apple")
                .withString("Flavour (taste)", "Yummy")
                .withDouble("Weight (lbs.)", 22.2)
                .withInt("[Qty]", 2)
                .withInt("_Qty_", 3)
                .build();

        NamedData bean = test.apply(data);

        assertThat(bean.getString("Fruit"), is("Apple"));
        assertThat(bean.getString("Flavour _taste_"), is("Yummy"));
        assertThat(bean.getDouble("Weight _lbs__"), is(22.2));
        assertThat(bean.getInt("_Qty_"), is(2));
        assertThat(bean.getInt("_Qty__"), is(3));
    }

}