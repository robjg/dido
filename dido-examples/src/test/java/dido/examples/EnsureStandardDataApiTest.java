package dido.examples;

import dido.data.*;
import dido.data.immutable.ArrayData;
import dido.data.immutable.MapData;
import dido.data.immutable.NonBoxedData;
import dido.data.schema.SchemaBuilder;
import dido.data.util.DataBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EnsureStandardDataApiTest {

    @ParameterizedTest
    @ValueSource(classes = {
            DidoData.class,
            ArrayData.class,
            NonBoxedData.class
    })
    void ensureStaticOfMethodsAreConsistent(Class<?> dataClass) throws NoSuchMethodException {

        assertThat(dataClass.isAssignableFrom(
                        dataClass.getMethod("of", Object[].class)
                                .getReturnType()),
                is(true));

    }

    @ParameterizedTest
    @ValueSource(classes = {
            DidoData.class,
            ArrayData.class,
            MapData.class,
            NonBoxedData.class})
    void ensureCreationMethodsAreConsistent(Class<?> dataClass) throws NoSuchMethodException {

        assertThat(DataBuilder.class.isAssignableFrom(
                        dataClass.getMethod("builderForSchema", DataSchema.class)
                                .getReturnType()),
                is(true));

        assertThat(DataBuilder.class.isAssignableFrom(
                        dataClass.getMethod("builder")
                                .getReturnType()),
                is(true));

        assertThat(FromValues.class.isAssignableFrom(
                        dataClass.getMethod("withSchema", DataSchema.class)
                                .getReturnType()),
                is(true));

        assertThat(dataClass.getMethod("copy", DidoData.class)
                        .getReturnType(),
                is(dataClass));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            ArrayData.class,
            MapData.class,
            NonBoxedData.class})
    void ensureStaticFactoryMethodsAreConsistent(Class<?> dataClass) throws NoSuchMethodException {

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

        assertThat(DataFactory.class.isAssignableFrom(
                        dataClass.getMethod("factoryForSchema", DataSchema.class)
                                .getReturnType()),
                is(true));

    }

}
