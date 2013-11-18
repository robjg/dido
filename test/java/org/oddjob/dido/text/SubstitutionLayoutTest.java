package org.oddjob.dido.text;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.state.ParentState;

public class SubstitutionLayoutTest extends TestCase {

	public static class Order {
		
		private Date date;
		
		private String fruit;
		
		private int quantity;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getFruit() {
			return fruit;
		}

		public void setFruit(String fruit) {
			this.fruit = fruit;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int qunatity) {
			this.quantity = qunatity;
		}
	}
	
	public void testRead() throws DataException {
		
		SimpleTextFieldsIn dataIn = new SimpleTextFieldsIn();
		
		dataIn.setValues(new String[] { "${favourite-fruit}" });
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("favourite-fruit", "Apple");
		
		SubstitutionLayout test = new SubstitutionLayout();
		
		test.setArooaSession(session);
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(dataIn);

		assertEquals("Apple", reader.read());
		
		assertEquals(null, reader.read());

		reader.close();
	}
	
	
	public void testWrite() throws DataException {
		
		SimpleTextFieldsOut dataOut = new SimpleTextFieldsOut();
				
		SubstitutionLayout test = new SubstitutionLayout();
		test.setSubstitution("${favourite-fruit}");
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		
		DataWriter writer = test.writerFor(dataOut);

		assertEquals(false, writer.write("Apple"));
		
		assertEquals("${favourite-fruit}", dataOut.values()[0]);
		
		writer.close();
	}
	
	public void testExample() throws ArooaPropertyException, ArooaConversionException, ParseException {
		
		File config = new File(getClass().getResource(
				"SubstitutionLayoutExample.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(config);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		@SuppressWarnings("unchecked")
		List<Order> orders = (List<Order>) lookup.lookup("read.beans", List.class);
		
		assertEquals(3, orders.size());
		
		Order order1 = orders.get(0);
		
		assertEquals(DateHelper.parseDate("2013-11-06"), order1.getDate());
		assertEquals("apples", order1.getFruit());
		assertEquals(5, order1.getQuantity());
		
		Order order2 = orders.get(1);
		
		assertEquals(DateHelper.parseDate("2013-11-06"), order2.getDate());
		assertEquals("bananas", order2.getFruit());
		assertEquals(2, order2.getQuantity());
		
		Order order3 = orders.get(2);
		
		assertEquals(DateHelper.parseDate("2013-11-06"), order3.getDate());
		assertEquals("pears", order3.getFruit());
		assertEquals(7, order3.getQuantity());
		
		String[] lines = lookup.lookup("write.data.output", String[].class);
		assertEquals("${order.date},apples,5", lines[0]);
		assertEquals("${order.date},bananas,2", lines[1]);
		assertEquals("${order.date},pears,7", lines[2]);

		oddjob.destroy();
	}
	
}
