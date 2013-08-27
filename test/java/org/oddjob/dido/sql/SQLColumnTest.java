package org.oddjob.dido.sql;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanViewBean;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.tabular.ColumnLayout;
import org.oddjob.state.ParentState;

public class SQLColumnTest extends TestCase {

	private static final Logger logger = Logger.getLogger(
			SQLColumnTest.class);

	public static class Fruit {
		
		private String type;
		
		private int quantity;

		public Fruit() {
		}
		
		public Fruit(String type, int quantity) {
			this.type = type;
			this.quantity = quantity;
		}
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int qunantity) {
			this.quantity = qunantity;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ", type=" + type;
		}
	}
	
	
	public void testWriteRead() throws DataException, ArooaPropertyException, ArooaConversionException {
		
		String config = getClass().getResource(
				"create_fruit_table.xml").getFile();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(new File(config));
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		ArooaValue connection = new OddjobLookup(oddjob
				).lookup("vars.connection", ArooaValue.class);
		
		ArooaSession session = new StandardArooaSession();
		
		SQLLayout test = new SQLLayout();
		test.setArooaSession(session);
		test.setWriteSQL("insert into fruit (type, quantity) values (?, ?)");
		test.setReadSQL("select type, quantity from fruit order by type");
		
		ColumnLayout<Object> column1 = new ColumnLayout<Object>();
		column1.setName("type");
		column1.setLabel("TYPE");
		
		ColumnLayout<Object> column2 = new ColumnLayout<Object>();
		column2.setName("quantity");
		column2.setLabel("QUANTITY");
		
		test.setOf(0, column1);
		test.setOf(1, column2);
		
		BeanBindingBean binding = new BeanBindingBean();
		binding.setArooaSession(session);
		test.bind(binding);
		
		ConnectionDataImpl connectionData = new ConnectionDataImpl();
		connectionData.setArooaSession(session);
		connectionData.setConnection(connection);
		
		logger.info("** Writing **");
		
		DataWriter writer = test.writerFor(connectionData);

		writer.write(new Fruit("apple", 20));
		writer.write(new Fruit("banana", 10));
		writer.write(new Fruit("orange", 102));

		writer.close();
		
		test.reset();
		binding.free();

		binding.setType(new SimpleArooaClass(Fruit.class));
		
		logger.info("** Reading **");
		
		DataReader reader = test.readerFor(connectionData);
		
		Fruit fruit = (Fruit) reader.read();
		
		assertEquals("apple", fruit.getType());
		assertEquals(20, fruit.getQuantity());
		
		fruit = (Fruit) reader.read();
		assertEquals("banana", fruit.getType());
		
		fruit = (Fruit) reader.read();
		assertEquals("orange", fruit.getType());
		

		fruit = (Fruit) reader.read();
		assertEquals(null, fruit);
		
		reader.close();
	}
	
	public void testWriteReadMorphic() throws DataException, ArooaPropertyException, ArooaConversionException {
		
		String config = getClass().getResource(
				"create_fruit_table.xml").getFile();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(new File(config));
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		ArooaValue connection = new OddjobLookup(oddjob
				).lookup("vars.connection", ArooaValue.class);
		
		ArooaSession session = new StandardArooaSession();
		
		SQLLayout test = new SQLLayout();
		test.setArooaSession(session);
		test.setWriteSQL("insert into fruit (type, quantity) values (?, ?)");
		test.setReadSQL("select type, quantity from fruit order by type");
		
		BeanViewBean beanView = new BeanViewBean();
		beanView.setProperties("type, quantity");
		
		BeanBindingBean binding = new BeanBindingBean();
		binding.setArooaSession(session);
		binding.setBeanView(beanView.toValue());
		
		test.bind(binding);
		
		ConnectionDataImpl connectionData = new ConnectionDataImpl();
		connectionData.setArooaSession(session);
		connectionData.setConnection(connection);
		
		logger.info("** Writing **");
		
		DataWriter writer = test.writerFor(connectionData);

		writer.write(new Fruit("apple", 20));
		writer.write(new Fruit("banana", 10));
		writer.write(new Fruit("orange", 102));

		writer.close();
		
		test.reset();
		binding.free();
		binding.setBeanView(null);
		
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		logger.info("** Reading **");
		
		DataReader reader = test.readerFor(connectionData);
		
		Object fruit = reader.read();
		
		assertEquals("apple", accessor.getProperty(fruit, "TYPE"));
		assertEquals(20, accessor.getProperty(fruit, "QUANTITY"));
		
		fruit = reader.read();
		assertEquals("banana", accessor.getProperty(fruit, "TYPE"));
		
		fruit = reader.read();
		assertEquals("orange", accessor.getProperty(fruit, "TYPE"));
		

		fruit = (Fruit) reader.read();
		assertEquals(null, fruit);
		
		reader.close();
	}
}
