package dido.examples;

import dido.csv.DataInCsv;
import dido.csv.DataOutCsv;
import dido.data.DataSchema;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.how.conversion.DidoConversionProvider;
import dido.json.DataInJson;
import dido.json.DataOutJson;
import dido.poi.DataInPoi;
import dido.poi.DataOutPoi;
import dido.sql.DataInSql;
import dido.sql.DataOutSql;
import dido.text.DataOutTextTable;
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

public class EnsureStandardInOutApiTest {

    @ParameterizedTest
    @ValueSource(classes = {
            DataInCsv.class, DataOutCsv.class,
            DataInJson.class, DataOutJson.class,
            DataInSql.class, DataOutSql.class,
            DataInPoi.class, DataOutPoi.class,
            DataOutTextTable.class
    })
    void ensureHowsStandardWith(Class<?> howClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object settings = howClass.getMethod("with")
                .invoke(null);

        assertThat(settings.getClass().getSimpleName(), is("Settings"));

        assertThat(settings.getClass().getMethod("make"),
                notNullValue());
    }


    @ParameterizedTest
    @ValueSource(classes = {
            DataInCsv.class,
            DataInJson.class,
            DataInPoi.class
    })
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

    }

    @ParameterizedTest
    @ValueSource(classes = {
            DataInCsv.class,
            DataInJson.class})
    void ensureMapFromStringAreConsistent(Class<?> howClass) throws NoSuchMethodException {

        assertThat(Function.class.isAssignableFrom(
                        howClass.getMethod("mapFromString")
                                .getReturnType()),
                is(true));

        Class<?> settingsClass = howClass.getMethod("with")
                .getReturnType();

        assertThat(Function.class.isAssignableFrom(
                        settingsClass.getMethod("mapFromString")
                                .getReturnType()),
                is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            DataInCsv.class,
            DataInPoi.class,
            DataOutPoi.class
    })
    void ensureHeaderMethodConsistent(Class<?> howClass) throws NoSuchMethodException {

        Class<?> settingsClass = howClass.getMethod("with")
                .getReturnType();

        assertThat(settingsClass.isAssignableFrom(
                        settingsClass.getMethod("header", boolean.class)
                                .getReturnType()),
                is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            DataInCsv.class,
            DataInPoi.class,
            DataOutPoi.class
    })
    void ensureConverterMethodConsistent(Class<?> howClass) throws NoSuchMethodException {

        Class<?> settingsClass = howClass.getMethod("with")
                .getReturnType();

        assertThat(settingsClass.isAssignableFrom(
                        settingsClass.getMethod("conversionProvider", DidoConversionProvider.class)
                                .getReturnType()),
                is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            DataOutCsv.class,
            DataOutJson.class,
            DataOutPoi.class,
            DataOutTextTable.class
    })
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

    }

    @ParameterizedTest
    @ValueSource(classes = {
            DataOutCsv.class,
            DataOutJson.class
    })
    void ensureMapToStringAreConsistent(Class<?> howClass) throws NoSuchMethodException {

        assertThat(Function.class.isAssignableFrom(
                        howClass.getMethod("mapToString")
                                .getReturnType()),
                is(true));

        Class<?> settingsClass = howClass.getMethod("with")
                .getReturnType();

        assertThat(Function.class.isAssignableFrom(
                        settingsClass.getMethod("mapToString")
                                .getReturnType()),
                is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            DataInCsv.class,
            DataInJson.class,
            DataInPoi.class
    })
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
    }

    @ParameterizedTest
    @ValueSource(classes = {
            DataOutCsv.class,
            DataOutJson.class,
            DataOutPoi.class,
            DataOutTextTable.class
    })
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
    }

    @ParameterizedTest
    @ValueSource(classes = {
            DataInCsv.class,
            DataInJson.class,
            DataInPoi.class
    })
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
    @ValueSource(classes = {
            DataOutCsv.class,
            DataOutJson.class,
            DataOutPoi.class,
            DataOutTextTable.class
    })
    void ensureOutSettingsAreConsistent(Class<?> howClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object settings = howClass.getMethod("with")
                .invoke(null);

        Class<?> settingsClass = settings.getClass();

        assertThat(settingsClass.getSimpleName(), is("Settings"));

        assertThat(settingsClass.getMethod("schema", DataSchema.class)
                        .getReturnType(),
                is(settingsClass));
    }

}
