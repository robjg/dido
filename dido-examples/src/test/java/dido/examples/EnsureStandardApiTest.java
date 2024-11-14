package dido.examples;

import dido.csv.DataInCsv;
import dido.csv.DataOutCsv;
import dido.data.*;
import dido.data.util.DataBuilder;
import dido.data.util.FieldValuesIn;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.json.DataInJson;
import dido.json.DataOutJson;
import dido.sql.DataInSql;
import dido.sql.DataOutSql;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class EnsureStandardApiTest {

    @ParameterizedTest
    @ValueSource(classes = {
            DataInCsv.class, DataOutCsv.class,
            DataInJson.class, DataOutJson.class,
            DataInSql.class, DataOutSql.class
            })
    void ensureHowsStandardWith(Class<?> howClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object settings = howClass.getMethod("with")
                .invoke(null);

        assertThat(settings.getClass().getSimpleName(), is("Settings"));

        assertThat(settings.getClass().getMethod("make"),
                notNullValue());
    }


    @ParameterizedTest
    @ValueSource(classes = {DataInCsv.class, DataInJson.class})
    void ensureIoStaticDataInsAreConsistent(Class<?> howClass) throws NoSuchMethodException {

        assertThat(DataIn.class.isAssignableFrom(
                        howClass.getMethod("fromReader", Reader.class)
                                .getReturnType()),
                is(true));

        assertThat(DataIn.class.isAssignableFrom(
                        howClass.getMethod("fromInputStream", InputStream.class)
                                .getReturnType()),
                is(true));

        assertThat(DataIn.class.isAssignableFrom(
                        howClass.getMethod("fromPath", Path.class)
                                .getReturnType()),
                is(true));

        assertThat(Function.class.isAssignableFrom(
                        howClass.getMethod("mapFromString")
                                .getReturnType()),
                is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {DataOutCsv.class, DataOutJson.class})
    void ensureIoStaticDataOutsAreConsistent(Class<?> howClass) throws NoSuchMethodException {

        assertThat(DataOut.class.isAssignableFrom(
                        howClass.getMethod("toAppendable", Appendable.class)
                                .getReturnType()),
                is(true));

        assertThat(DataOut.class.isAssignableFrom(
                        howClass.getMethod("toOutputStream", OutputStream.class)
                                .getReturnType()),
                is(true));

        assertThat(DataOut.class.isAssignableFrom(
                        howClass.getMethod("toPath", Path.class)
                                .getReturnType()),
                is(true));

        assertThat(Function.class.isAssignableFrom(
                        howClass.getMethod("mapToString")
                                .getReturnType()),
                is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {DataInCsv.class, DataInJson.class})
    void ensureIoWithDataInsAreConsistent(Class<?> howClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object settings = howClass.getMethod("with")
                .invoke(null);

        Class<?> settingsClass = settings.getClass();

        assertThat(settingsClass.getSimpleName(), is("Settings"));

        assertThat(DataIn.class.isAssignableFrom(
                        settingsClass.getMethod("fromReader", Reader.class)
                                .getReturnType()),
                is(true));

        assertThat(DataIn.class.isAssignableFrom(
                        settingsClass.getMethod("fromInputStream", InputStream.class)
                                .getReturnType()),
                is(true));

        assertThat(DataIn.class.isAssignableFrom(
                        settingsClass.getMethod("fromPath", Path.class)
                                .getReturnType()),
                is(true));

        assertThat(Function.class.isAssignableFrom(
                        settingsClass.getMethod("mapFromString")
                                .getReturnType()),
                is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {DataOutCsv.class, DataOutJson.class})
    void ensureIoWithDataOutsAreConsistent(Class<?> howClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object settings = howClass.getMethod("with")
                .invoke(null);

        Class<?> settingsClass = settings.getClass();

        assertThat(settingsClass.getSimpleName(), is("Settings"));

        assertThat(DataOut.class.isAssignableFrom(
                        settingsClass.getMethod("toAppendable", Appendable.class)
                                .getReturnType()),
                is(true));

        assertThat(DataOut.class.isAssignableFrom(
                        settingsClass.getMethod("toOutputStream", OutputStream.class)
                                .getReturnType()),
                is(true));

        assertThat(DataOut.class.isAssignableFrom(
                        settingsClass.getMethod("toPath", Path.class)
                                .getReturnType()),
                is(true));

        assertThat(Function.class.isAssignableFrom(
                        settingsClass.getMethod("mapToString")
                                .getReturnType()),
                is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {DataInCsv.class, DataInJson.class})
    void ensureInSettingsAreConsistent(Class<?> howClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object settings = howClass.getMethod("with")
                .invoke(null);

        Class<?> settingsClass = settings.getClass();

        assertThat(settingsClass.getSimpleName(), is("Settings"));

        assertThat(settingsClass.getMethod("schema", DataSchema.class)
                        .getReturnType(),
                is(settingsClass));

        assertThat(settingsClass.getMethod("partialSchema", boolean.class)
                        .getReturnType(),
                is(settingsClass));
    }

    @ParameterizedTest
    @ValueSource(classes = {DataOutCsv.class, DataOutJson.class})
    void ensureOutSettingsAreConsistent(Class<?> howClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object settings = howClass.getMethod("with")
                .invoke(null);

        Class<?> settingsClass = settings.getClass();

        assertThat(settingsClass.getSimpleName(), is("Settings"));

        assertThat(settingsClass.getMethod("schema", DataSchema.class)
                        .getReturnType(),
                is(settingsClass));
    }

    @ParameterizedTest
    @ValueSource(classes = {ArrayData.class, MapData.class, NonBoxedData.class})
    void ensureDataStaticMethodsAreConsistent(Class<?> dataClass) throws NoSuchMethodException {

        assertThat(SchemaFactory.class.isAssignableFrom(
                        dataClass.getMethod("schemaFactory")
                                .getReturnType()),
                is(true));

        assertThat(SchemaBuilder.class.isAssignableFrom(
                        dataClass.getMethod("schemaBuilder")
                                .getReturnType()),
                is(true));

        String dataName = dataClass.getSimpleName();

        assertThat(DataSchema.class.isAssignableFrom(
                        dataClass.getMethod("as" + dataName + "Schema", DataSchema.class)
                                .getReturnType()),
                is(true));

        assertThat(DataBuilder.class.isAssignableFrom(
                        dataClass.getMethod("builderForSchema", DataSchema.class)
                                .getReturnType()),
                is(true));

        assertThat(DataBuilder.class.isAssignableFrom(
                        dataClass.getMethod("builderNoSchema")
                                .getReturnType()),
                is(true));

        assertThat(DataFactory.class.isAssignableFrom(
                        dataClass.getMethod("factoryForSchema", DataSchema.class)
                                .getReturnType()),
                is(true));

        assertThat(FieldValuesIn.class.isAssignableFrom(
                        dataClass.getMethod("valuesForSchema", DataSchema.class)
                                .getReturnType()),
                is(true));

        assertThat(dataClass.getMethod("copy", DidoData.class)
                                .getReturnType(),
                is(dataClass));
    }
}
