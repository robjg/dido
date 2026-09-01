package dido.sql;

import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.how.DataOutHow;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.state.ParentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class SqlOutExceptionTest {

    private static final Logger logger = LoggerFactory.getLogger(
            SqlDataPreparedTest.class);

    @Test
    void simpleWriteRead() throws Exception {

        String config = Objects.requireNonNull(getClass().getResource(
                "create_fruit_table.xml")).getFile();

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(config));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        List<Exception> exceptions = new ArrayList<>();

        DataOutHow<Connection> outHow
                = DataOutSql.with()
                .sql("insert into fruit (type, quantity) values (?, ?)")
                .exceptionListener(exceptions::add)
                .make();

        DataInSql inHow
                = DataInSql.with()
                .sql("select type as \"type\", quantity as \"quantity\" from fruit order by type")
                .make();

        Connection connectionOut = lookup.lookup("vars.connection", Connection.class);

        DataOut writer = outHow.outTo(connectionOut);

        writer.accept(ArrayData.of("apple", 20));
        writer.accept(ArrayData.of("apple", 30));
        writer.accept(ArrayData.of("orange", 102));

        writer.close();

        assertThat(exceptions.size(), is(1));

        logger.info("** simpleWriteRead - Reading **");

        Connection connectionIn = lookup.lookup("vars.connection", Connection.class);

        DataIn reader = inHow.inFrom(connectionIn);

        List<DidoData> results = reader.stream()
                .map(ArrayData::copy)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("apple", 20), DidoData.of("orange", 102)));


        reader.close();
    }

}
