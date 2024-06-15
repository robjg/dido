package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.GenericDataSchema;
import dido.data.GenericSchemaField;
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
        GenericDataSchema<String> schema = lookup.lookup("vars.schema", GenericDataSchema.class);

        assertThat(schema, notNullValue());
        assertThat(schema.getTypeOf("Fruit"), is(String.class));
        assertThat(schema.getTypeOf("Qty"), is(int.class));
        assertThat(schema.getTypeOf("Price"), is(double.class));
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
        GenericDataSchema<String> schema = lookup.lookup("vars.schema", GenericDataSchema.class);

        assertThat(schema, notNullValue());
        assertThat(schema.getTypeOf("Name"), is(String.class));

        DataSchema fruitSchema = schema.getSchema("Fruit1");
        assertThat(fruitSchema.getTypeNamed("Fruit"), is(String.class));
        assertThat(fruitSchema.getTypeNamed("Qty"), is(int.class));

        assertThat(schema.getSchema("Fruit2"), sameInstance(fruitSchema));

        DataSchema drinkSchema = schema.getSchema("Drink");
        assertThat(drinkSchema.getTypeNamed("Volume"), is(double.class));

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
        GenericDataSchema<String> schema = lookup.lookup("vars.schema", GenericDataSchema.class);

        assertThat(schema, notNullValue());
        assertThat(schema.getTypeOf("Name"), is(String.class));

        assertThat(schema.getTypeOf("Fruit"), is(GenericSchemaField.NESTED_REPEATING_TYPE));
        DataSchema fruitSchema = schema.getSchema("Fruit");
        assertThat(fruitSchema.getTypeNamed("Fruit"), is(String.class));
        assertThat(fruitSchema.getTypeNamed("Qty"), is(int.class));

        assertThat(schema.getTypeOf("Drink"), is(GenericSchemaField.NESTED_REPEATING_TYPE));
        DataSchema drinkSchema = schema.getSchema("Drink");
        assertThat(drinkSchema.getTypeNamed("Volume"), is(double.class));

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
        GenericDataSchema<String> schema = lookup.lookup("vars.schema", GenericDataSchema.class);

        assertThat(schema, notNullValue());

        assertThat(schema.getTypeOf("OrderLines"), is(GenericSchemaField.NESTED_REPEATING_TYPE));
        DataSchema nestedSchema = schema.getSchema("OrderLines");
        assertThat(nestedSchema, is(GenericDataSchema.emptySchema()));

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
        GenericDataSchema<String> schema = lookup.lookup("main/vars.schema", GenericDataSchema.class);

        assertThat(schema, notNullValue());
        assertThat(schema.getTypeOf("CustomType").getName(), is("foo.stuff.SomeType"));

        oddjob.destroy();
    }
}
