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
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;

class ValueCopyFactoryTest {

    @Test
    void testConvertingStringToIntegerType() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory test =  new ValueCopyFactory();
        test.setField("Foo");
        test.setType(Integer.class);
        test.setTo("FooAmount");

        TransformerFactory transformerFactory = test.get();

        DataSchema inSchema = SchemaBuilder.newInstance()
                .addNamed("Foo", String.class)
                .build();

        SchemaSetter schemaSetter = mock(SchemaSetter.class);

        Transformer transformer = transformerFactory.create(inSchema, schemaSetter);

        verify(schemaSetter).addField(SchemaField.of(1, "FooAmount", Integer.class));

        Setter dataSetter = mock(Setter.class);
        DataFactory<?> dataFactory = mock(DataFactory.class);
        when(dataFactory.getSetterNamed("FooAmount")).thenReturn(dataSetter);

        DidoData data = MapData.of("Foo", "423");

        Consumer<DidoData> consumer = transformer.transform(dataFactory);
        consumer.accept(data);

        verify(dataSetter).set(423);
    }

    @Test
    void testConvertingStringToIntType() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory test =  new ValueCopyFactory();
        test.setField("Foo");
        test.setType(int.class);
        test.setTo("FooAmount");

        TransformerFactory transformerFactory = test.get();

        DataSchema inSchema = SchemaBuilder.newInstance()
                .addNamed("Foo", String.class)
                .build();

        SchemaSetter schemaSetter = mock(SchemaSetter.class);

        Transformer transformer = transformerFactory.create(inSchema, schemaSetter);

        verify(schemaSetter).addField(SchemaField.of(1, "FooAmount", int.class));

        Setter dataSetter = mock(Setter.class);
        DataFactory<?> dataFactory = mock(DataFactory.class);
        when(dataFactory.getSetterNamed("FooAmount")).thenReturn(dataSetter);

        DidoData data = MapData.of("Foo", "423");

        Consumer<DidoData> consumer = transformer.transform(dataFactory);
        consumer.accept(data);

        verify(dataSetter).set(423);
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


        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("AByte", byte.class)
                .addNamed("AShort", short.class)
                .addNamed("AChar", char.class)
                .addNamed("AnInt", int.class)
                .addNamed("ALong", long.class)
                .addNamed("ADouble", double.class)
                .addNamed("AFloat", float.class)
                .addNamed("ABoolean", boolean.class)
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


        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("SomeOne", int.class)
                .addNamed("Two", int.class)
                .addNamed("SomeThree", int.class)
                .addNamed("Four", int.class)
                .addNamed("Five", int.class)
                .addNamed("SomeSix", int.class)
                .addNamed("Seven", int.class)
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


        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("AnInt", int.class)
                .addNamed("BlankString", String.class)
                .addNamed("CsvString", int[].class)
                .addNamed("NullString", String.class)
                .build();

        DataSchema schema = result1.getSchema();

        assertThat(schema, is(expectedSchema));

        assertThat(result1.getNamed("AnInt"), is(2));
        assertThat(result1.getNamed("BlankString"), nullValue());
        assertThat(result1.getNamed("CsvString"), is(new int[] { 1, 2 }));
        assertThat(result1.getNamed("NullString"), is("Nada"));

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