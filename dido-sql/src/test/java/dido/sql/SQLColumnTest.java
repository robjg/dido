package dido.sql;

import dido.data.ArrayData;
import dido.data.GenericData;
import dido.how.DataIn;
import dido.how.DataInHow;
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
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class SQLColumnTest {

    private static final Logger logger = LoggerFactory.getLogger(
            SQLColumnTest.class);

    @Test
    public void testWriteRead() throws Exception {

        String config = Objects.requireNonNull(getClass().getResource(
                "create_fruit_table.xml")).getFile();

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(config));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        DataOutHow<String, Connection> outHow
                = SqlDataOutHow.fromSql("insert into fruit (type, quantity) values (?, ?)")
                .make();

        DataInHow<String, Connection> inHow
                = SqlDataInHow.fromSql("select type, quantity from fruit order by type")
                .make();

        logger.info("** Writing **");

        Connection connectionOut = lookup.lookup("vars.connection", Connection.class);

        DataOut<String> writer = outHow.outTo(connectionOut);

        writer.accept(ArrayData.of("apple", 20));
        writer.accept(ArrayData.of("banana", 10));
        writer.accept(ArrayData.of("orange", 102));

        writer.close();

        logger.info("** Reading **");

        Connection connectionIn = lookup.lookup("vars.connection", Connection.class);

        DataIn<String> reader = inHow.inFrom(connectionIn);

        {
            GenericData<String> fruit = reader.get();
            assertThat(fruit.getString("type"), is("apple"));
            assertThat(fruit.getInt("quantity"), is(20));
        }
        {
            GenericData<String> fruit = reader.get();
            assertThat(fruit.getString("type"), is("banana"));
            assertThat(fruit.getInt("quantity"), is(10));
        }
        {
            GenericData<String> fruit = reader.get();
            assertThat(fruit.getString("type"), is("orange"));
            assertThat(fruit.getInt("quantity"), is(102));
        }

        assertThat(reader.get(), nullValue());

        reader.close();
    }

//    public void testWriteReadMorphic() throws DataException, ArooaPropertyException, ArooaConversionException {
//
//        String config = getClass().getResource(
//                "create_fruit_table.xml").getFile();
//
//        Oddjob oddjob = new Oddjob();
//        oddjob.setFile(new File(config));
//
//        oddjob.run();
//
//        assertEquals(ParentState.COMPLETE,
//                oddjob.lastStateEvent().getState());
//
//        ArooaValue connection = new OddjobLookup(oddjob
//        ).lookup("vars.connection", ArooaValue.class);
//
//        ArooaSession session = new StandardArooaSession();
//
//        SQLLayout test = new SQLLayout();
//        test.setArooaSession(session);
//        test.setWriteSQL("insert into fruit (type, quantity) values (?, ?)");
//        test.setReadSQL("select type, quantity from fruit order by type");
//
//        BeanViewBean beanView = new BeanViewBean();
//        beanView.setProperties("type, quantity");
//
//        BeanBindingBean binding = new BeanBindingBean();
//        binding.setArooaSession(session);
//        binding.setBeanView(beanView.toValue());
//
//        test.setBinding(binding);
//
//        ConnectionDataImpl connectionData = new ConnectionDataImpl();
//        connectionData.setArooaSession(session);
//        connectionData.setConnection(connection);
//
//        logger.info("** Writing **");
//
//        DataWriter writer = test.writerFor(connectionData);
//
//        writer.write(new Fruit("apple", 20));
//        writer.write(new Fruit("banana", 10));
//        writer.write(new Fruit("orange", 102));
//
//        writer.close();
//
//        test.reset();
//        binding.free();
//        binding.setBeanView(null);
//
//        PropertyAccessor accessor = session.getTools().getPropertyAccessor();
//
//        logger.info("** Reading **");
//
//        DataReader reader = test.readerFor(connectionData);
//
//        Object fruit = reader.read();
//
//        assertEquals("apple", accessor.getProperty(fruit, "TYPE"));
//        assertEquals(20, accessor.getProperty(fruit, "QUANTITY"));
//
//        fruit = reader.read();
//        assertEquals("banana", accessor.getProperty(fruit, "TYPE"));
//
//        fruit = reader.read();
//        assertEquals("orange", accessor.getProperty(fruit, "TYPE"));
//
//
//        fruit = (Fruit) reader.read();
//        assertEquals(null, fruit);
//
//        reader.close();
//    }
}
