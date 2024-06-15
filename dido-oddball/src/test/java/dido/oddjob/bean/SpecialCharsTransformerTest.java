package dido.oddjob.bean;

import dido.data.DidoData;
import dido.data.GenericDataSchema;
import dido.data.MapData;
import dido.data.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SpecialCharsTransformerTest {

    @Test
    public void testBuildWithReplacements() {

        GenericDataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("Fruit", String.class)
                .addField("Flavour (taste)", String.class)
                .addField("Weight (lbs.)", double.class)
                .addField("[Qty]", int.class)
                .addField("_Qty_", int.class)
                .build();

        Function<DidoData, DidoData> test = new SpecialCharsTransformer().toValue();

        DidoData data = MapData.newBuilder(schema)
                .setString("Fruit", "Apple")
                .setString("Flavour (taste)", "Yummy")
                .setDouble("Weight (lbs.)", 22.2)
                .setInt("[Qty]", 2)
                .setInt("_Qty_", 3)
                .build();

        DidoData bean = test.apply(data);

        assertThat(bean.getString("Fruit"), is("Apple"));
        assertThat(bean.getString("Flavour _taste_"), is("Yummy"));
        assertThat(bean.getDouble("Weight _lbs__"), is(22.2));
        assertThat(bean.getInt("_Qty_"), is(2));
        assertThat(bean.getInt("_Qty__"), is(3));
    }

}