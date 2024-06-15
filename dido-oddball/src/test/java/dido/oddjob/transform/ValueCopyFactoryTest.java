package dido.oddjob.transform;

import dido.data.*;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.state.ParentState;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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

        TransformerFactory transformerFactory = test.toValue();

        DataSchema inSchema = SchemaBuilder.forStringFields()
                .addField("Foo", String.class)
                .build();

        SchemaSetter schemaSetter = mock(SchemaSetter.class);

        Transformer transformer = transformerFactory.create(1, inSchema, schemaSetter);

        verify(schemaSetter).setFieldAt(1, "FooAmount", Integer.class);

        DataSetter dataSetter = mock(DataSetter.class);

        DidoData data = MapData.of("Foo", "423");

        transformer.transform(data, dataSetter);

        verify(dataSetter).set("FooAmount", 423);
    }

    @Test
    void testConvertingStringToIntType() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory test =  new ValueCopyFactory();
        test.setArooaSession(session);
        test.setType(int.class);
        test.setTo("FooAmount");

        TransformerFactory transformerFactory = test.toValue();

        DataSchema inSchema = SchemaBuilder.forStringFields()
                .addField("Foo", String.class)
                .build();

        SchemaSetter schemaSetter = mock(SchemaSetter.class);

        Transformer transformer = transformerFactory.create(1, inSchema, schemaSetter);

        verify(schemaSetter).setFieldAt(1, "FooAmount", int.class);

        DataSetter dataSetter = mock(DataSetter.class);

        DidoData data = MapData.of("Foo", "423");

        transformer.transform(data, dataSetter);

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

        DidoData result1 = lookup.lookup("results.list.value[0]", DidoData.class);


        DataSchema expectedSchema = SchemaBuilder.forStringFields()
                .addField("AByte", byte.class)
                .addField("AShort", short.class)
                .addField("AChar", char.class)
                .addField("AnInt", int.class)
                .addField("ALong", long.class)
                .addField("ADouble", double.class)
                .addField("AFloat", float.class)
                .addField("ABoolean", boolean.class)
                .build();


        DataSchema schema = result1.getSchema();

        assertThat(schema, is(expectedSchema));

        DidoData expectedData = ArrayData.valuesFor(schema)
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

        DidoData result1 = lookup.lookup("results.list.value[0]", DidoData.class);


        DataSchema expectedSchema = SchemaBuilder.forStringFields()
                .addField("SomeOne", int.class)
                .addField("Two", int.class)
                .addField("SomeThree", int.class)
                .addField("Four", int.class)
                .addField("Five", int.class)
                .addField("SomeSix", int.class)
                .addField("Seven", int.class)
                .build();


        DataSchema schema = result1.getSchema();

        assertThat(schema, is(expectedSchema));

        DidoData expectedData = ArrayData.valuesFor(schema)
                .of(1, 2, 3, 4, 5, 6,  7);

        assertThat(result1, is(expectedData));

        oddjob.destroy();

    }

    @Test
    void testCopyFunctionExample() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataCopyFunctionExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        DidoData result1 = lookup.lookup("results.list.value[0]", DidoData.class);


        DataSchema expectedSchema = SchemaBuilder.forStringFields()
                .addField("AnInt", int.class)
                .addField("BlankString", String.class)
                .addField("CsvString", int[].class)
                .addField("NullString", String.class)
                .build();

        DataSchema schema = result1.getSchema();

        assertThat(schema, is(expectedSchema));

        assertThat(result1.get("AnInt"), is(2));
        assertThat(result1.get("BlankString"), nullValue());
        assertThat(result1.get("CsvString"), is(new int[] { 1, 2 }));
        assertThat(result1.get("NullString"), is("Nada"));

        oddjob.destroy();

    }

    public static class AddOne implements Function<Integer, Integer> {

        @Override
        public Integer apply(Integer integer) {
            return integer + 1;
        }
    }

    public static class NullWhenBlank implements Function<String, String> {

        @Override
        public String apply(String s) {
            if (s.isBlank()) {
                return null;
            }
            else {
                return s;
            }
        }
    }

    public static class SplitAndConvert implements Function<String, int[]> {

        @Override
        public int[] apply(String s) {
            return Arrays.stream(s.split(","))
                    .mapToInt(Integer::valueOf)
                    .toArray();
        }
    }

    public static class WhenNull implements Function<Object, String> {

        @Override
        public String apply(Object o) {
            return Objects.requireNonNullElse(o, "Nada").toString();
        }
    }
}