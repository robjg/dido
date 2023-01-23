package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.test.OurDirs;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.tools.CompileJob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

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

    @Test
    void testRepeatingSchema() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("RepeatingSchema.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        DataSchema<String> schema = lookup.lookup("vars.schema", DataSchema.class);

        assertThat(schema, notNullValue());
        assertThat(schema.getType("Name"), is(String.class));

        assertThat(schema.getType("Fruit"), is(SchemaField.NESTED_REPEATING_TYPE));
        DataSchema<String> fruitSchema = schema.getSchema("Fruit");
        assertThat(fruitSchema.getType("Fruit"), is(String.class));
        assertThat(fruitSchema.getType("Qty"), is(int.class));

        assertThat(schema.getType("Drink"), is(SchemaField.NESTED_REPEATING_TYPE));
        DataSchema<String> drinkSchema = schema.getSchema("Drink");
        assertThat(drinkSchema.getType("Volume"), is(double.class));

        oddjob.destroy();
    }

    @Test
    void testEmptyNestedRepeatingSchema() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("EmptyRepeatingSchema.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        DataSchema<String> schema = lookup.lookup("vars.schema", DataSchema.class);

        assertThat(schema, notNullValue());

        assertThat(schema.getType("OrderLines"), is(SchemaField.NESTED_REPEATING_TYPE));
        DataSchema<String> nestedSchema = schema.getSchema("OrderLines");
        assertThat(nestedSchema, is(DataSchema.emptySchema()));

        oddjob.destroy();
    }

    @Test
    void typeFromOddjobCustomClassLoader() throws ArooaConversionException, IOException {

        Path workDir = OurDirs.workPathDir(
                getClass().getSimpleName() + "-typeFromOddjobCustomClassLoader",
                true);

        CompileJob compileJob = new CompileJob();
        compileJob.setFiles(new File[] {
                new File(Objects.requireNonNull(
                        getClass().getResource("/types/foo/stuff/SomeType.java")).getFile())
        });
        compileJob.setDest(workDir.toFile());
        compileJob.run();

        Properties properties = new Properties();
        properties.setProperty("class.dir", workDir.toAbsolutePath().toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setProperties(properties);
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("ClassPathTypeSchema.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        DataSchema<String> schema = lookup.lookup("main/vars.schema", DataSchema.class);

        assertThat(schema, notNullValue());
        assertThat(schema.getType("CustomType").getName(), is("foo.stuff.SomeType"));

        oddjob.destroy();
    }
}
