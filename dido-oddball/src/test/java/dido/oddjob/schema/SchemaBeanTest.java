package dido.oddjob.schema;

import dido.data.DataSchema;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SchemaBeanTest {

    @Test
    void testSimpleSchema() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("SimpleSchema.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        DataSchema<String> schema = lookup.lookup("vars.schema", DataSchema.class);

        assertThat(schema, notNullValue());
        assertThat(schema.getType("Fruit"), is(String.class));
        assertThat(schema.getType("Qty"), is(int.class));
        assertThat(schema.getType("Price"), is(double.class));
    }

    @Test
    void testNestedSchema() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("NestedSchema.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        DataSchema<String> schema = lookup.lookup("vars.schema", DataSchema.class);

        assertThat(schema, notNullValue());
        assertThat(schema.getType("Name"), is(String.class));

        DataSchema<String> fruitSchema = schema.getSchema("Fruit1");
        assertThat(fruitSchema.getType("Fruit"), is(String.class));
        assertThat(fruitSchema.getType("Qty"), is(int.class));

        assertThat(schema.getSchema("Fruit2"), sameInstance(fruitSchema));

        DataSchema<String> drinkSchema = schema.getSchema("Drink");
        assertThat(drinkSchema.getType("Volume"), is(double.class));

        oddjob.destroy();
    }

}
