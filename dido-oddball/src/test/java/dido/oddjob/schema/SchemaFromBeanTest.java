package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.NoSuchFieldException;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaFromBeanTest {

    @Test
    void include() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("SchemaFromInclude.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));
    }

    @Test
    void exclude() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("SchemaFromExclude.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));
    }

    @Test
    void merge() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("SchemaFromMerge.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));
    }

    @Test
    void concat() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("SchemaFromConcat.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));
    }

    @Test
    void noIncludeField() {

        DataSchema schema = DataSchema.builder().build();

        SchemaFromBean schemaFromBean = new SchemaFromBean();
        schemaFromBean.setFrom(schema);
        schemaFromBean.setInclude(new String[] { "Foo" });

        NoSuchFieldException thrown = assertThrows(
                NoSuchFieldException.class,
                schemaFromBean::toSchema,
                "Expected to throw, but it didn't"
        );

        assertThat(thrown.getMessage(), is(
                "No such field named [Foo], schema is {}"));
    }

    @Test
    void noExcludeField() {

        DataSchema schema = DataSchema.builder().build();

        SchemaFromBean schemaFromBean = new SchemaFromBean();
        schemaFromBean.setFrom(schema);
        schemaFromBean.setExclude(new String[] { "Foo" });

        NoSuchFieldException thrown = assertThrows(
                NoSuchFieldException.class,
                schemaFromBean::toSchema,
                "Expected to throw, but it didn't"
        );

        assertThat(thrown.getMessage(), is(
                "No such field named [Foo], schema is {}"));
    }
}