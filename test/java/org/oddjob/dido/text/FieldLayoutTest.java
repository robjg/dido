package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.Helper;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.stream.ListLinesOut;

public class FieldLayoutTest extends TestCase {

	public void testWriteSimpleFields() throws DataException {
		
		SimpleFieldsOut fields = new SimpleFieldsOut();
		
		FieldLayout test = new FieldLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);

		DataWriter writer = test.writerFor(fields);
		
		assertEquals(false, writer.write("Apples"));

		String[] results = fields.values();
		
		assertEquals("Apples", results[0]);
		assertEquals(1, results.length);
		
		String[] headings = fields.headings();
		assertEquals(0, headings.length);
	}
	
	public void testWriteSimpleFieldByLabel() throws DataException {
		
		SimpleFieldsOut fields = new SimpleFieldsOut();
		
		FieldLayout test = new FieldLayout();
		test.setName("fruit");
		test.setLabel("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);

		DataWriter writer = test.writerFor(fields);
		
		assertEquals(false, writer.write("Apples"));

		String[] results = fields.values();
		
		assertEquals("Apples", results[0]);
		assertEquals(1, results.length);
		
		String[] headings = fields.headings();
		assertEquals("Fruit", headings[0]);
		assertEquals(1, headings.length);
	}
	
	public void testReadSimpleFields() throws DataException {
		
		SimpleFieldsIn fields = new SimpleFieldsIn();
		fields.setValues(new String[] { "Apple" });
		
		FieldLayout test = new FieldLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(fields);
		
		Object result = reader.read();
		
		assertEquals("Apple", result);
		
		assertNull(reader.read());
	}
	
	public void testReadSimpleFieldsByLabel() throws DataException {
		
		SimpleFieldsIn fields = new SimpleFieldsIn();
		fields.setHeadings(new String[] { "Stuff", "Fruit" });
		fields.setValues(new String[] { "Foo", "Apple" });
		
		FieldLayout test = new FieldLayout();
		test.setLabel("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(fields);
		
		Object result = reader.read();
		
		assertEquals("Apple", result);
		
		assertNull(reader.read());
	}
	
	public static class Fruit {
		
		private String fruit;
		
		private String colour;
		
		public Fruit(String fruit, String colour) {
			this.fruit = fruit;
			this.colour = colour;
		}
		
		public String getFruit() {
			return fruit;
		}
		
		public String getColour() {
			return colour;
		}
	}
	
	public void testWriteWhenChildOfATextField() throws ArooaParseException, DataException {
		
		String xml = 
				"<dido:delimited xmlns:dido='oddjob:dido'>" +
				" <of>" +
				"  <dido:field>" +
				"   <of>" +
				"    <dido:delimited delimiter='|'>" +
				"     <of>" +
				"      <dido:field name='fruit'/>" +
				"      <dido:field name='colour'/>" +
				"     </of>" +
				"    </dido:delimited>" +
				"   </of>" +
				"  </dido:field>" +
				" </of>" +
				"</dido:delimited>";
		
		Layout layout = (Layout) 
				Helper.createTypeFromConfiguration(new XMLConfiguration("XML", xml));
	
		BeanBindingBean binding = new BeanBindingBean();
		binding.setArooaSession(new StandardArooaSession());
		binding.setType(new SimpleArooaClass(Fruit.class));
		
		layout.bind(binding);
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = layout.writerFor(dataOut);
		
		assertEquals(true, writer.write(new Fruit("apples", "green")));
		assertEquals(true, writer.write(new Fruit("plums", "red")));
		assertEquals(true, writer.write(new Fruit("bananas", "yellow")));
		
		writer.close();
		
		assertEquals("apples|green", dataOut.getLines().get(0));
		assertEquals("plums|red", dataOut.getLines().get(1));
		assertEquals("bananas|yellow", dataOut.getLines().get(2));
		assertEquals(3, dataOut.getLines().size());
	}
}
