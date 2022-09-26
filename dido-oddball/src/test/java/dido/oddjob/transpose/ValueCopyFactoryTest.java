package dido.oddjob.transpose;

import dido.data.*;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.state.ParentState;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ValueCopyFactoryTest {

    @Test
    void testConvertingStringToIntegerType() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory test =  new ValueCopyFactory();
        test.setArooaSession(session);
        test.setType(Integer.class);
        test.setTo("FooAmount");

        TransposerFactory<String, String> transposerFactory = test.toValue();

        DataSchema<String> inSchema = SchemaBuilder.forStringFields()
                .addField("Foo", String.class)
                .build();

        SchemaSetter<String> schemaSetter = mock(SchemaSetter.class);

        Transposer<String, String> transposer = transposerFactory.create(1, inSchema, schemaSetter);

        verify(schemaSetter).setFieldAt(1, "FooAmount", Integer.class);

        DataSetter<String> dataSetter = mock(DataSetter.class);

        GenericData<String> data = MapData.of("Foo", "423");

        transposer.transpose(data, dataSetter);

        verify(dataSetter).set("FooAmount", 423);
    }

    @Test
    void testConvertingStringToIntType() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory test =  new ValueCopyFactory();
        test.setArooaSession(session);
        test.setType(int.class);
        test.setTo("FooAmount");

        TransposerFactory<String, String> transposerFactory = test.toValue();

        DataSchema<String> inSchema = SchemaBuilder.forStringFields()
                .addField("Foo", String.class)
                .build();

        SchemaSetter<String> schemaSetter = mock(SchemaSetter.class);

        Transposer<String, String> transposer = transposerFactory.create(1, inSchema, schemaSetter);

        verify(schemaSetter).setFieldAt(1, "FooAmount", int.class);

        DataSetter<String> dataSetter = mock(DataSetter.class);

        GenericData<String> data = MapData.of("Foo", "423");

        transposer.transpose(data, dataSetter);

        verify(dataSetter).set("FooAmount", 423);
    }

    @Test
    void testCopyToPrimitivesExample() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataCopyToPrimitivesExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        GenericData<String> result1 = lookup.lookup("results.list.value[0]", GenericData.class);


        DataSchema<String> expectedSchema = SchemaBuilder.forStringFields()
                .addField("AByte", byte.class)
                .addField("AShort", short.class)
                .addField("AChar", char.class)
                .addField("AnInt", int.class)
                .addField("ALong", long.class)
                .addField("ADouble", double.class)
                .addField("AFloat", float.class)
                .addField("ABoolean", boolean.class)
                .build();


        DataSchema<String> schema = result1.getSchema();

        assertThat(schema, is(expectedSchema));

        GenericData<String> expectedData = ArrayData.valuesFor(schema)
                .of((byte) 65, (short) 65, '6', 65, 65L, 456.57, 456.57f, true);

        assertThat(result1, is(expectedData));

        oddjob.destroy();

    }

    @Test
    void testCopyToDifferentNamesExample() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataCopyToDifferentNamesExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        GenericData<String> result1 = lookup.lookup("results.list.value[0]", GenericData.class);


        DataSchema<String> expectedSchema = SchemaBuilder.forStringFields()
                .addField("SomeOne", int.class)
                .addField("Two", int.class)
                .addField("SomeThree", int.class)
                .addField("Four", int.class)
                .addField("Five", int.class)
                .addField("SomeSix", int.class)
                .addField("Seven", int.class)
                .build();


        DataSchema<String> schema = result1.getSchema();

        assertThat(schema, is(expectedSchema));

        GenericData<String> expectedData = ArrayData.valuesFor(schema)
                .of(1, 2, 3, 4, 5, 6,  7);

        assertThat(result1, is(expectedData));

        oddjob.destroy();

    }
}