package org.oddjob.dido.text;

import junit.framework.TestCase;

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
import org.oddjob.tools.OddjobTestHelper;

public class FieldLayoutTest extends TestCase {

	public void testOutput() throws DataException {
		
		TextLayout test = new TextLayout();

		test.setBinding(new DirectBinding());
		
		SimpleTextFieldsOut dataOut = new SimpleTextFieldsOut();
		
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write("apples");
		
		assertEquals(1, test.getIndex());
		
		assertEquals("apples", dataOut.values()[0]);		
		
		writer.write("oranges");
				
		assertEquals("oranges", dataOut.values()[0]);
	}
	
	public void testWriteSimpleFields() throws DataException {
		
		SimpleTextFieldsOut fields = new SimpleTextFieldsOut();
		
		TextLayout test = new TextLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.setBinding(binding);

		DataWriter writer = test.writerFor(fields);
		
		assertEquals(false, writer.write("Apples"));

		String[] results = fields.values();
		
		assertEquals("Apples", results[0]);
		assertEquals(1, results.length);
		
		String[] headings = fields.headings();
		assertEquals(0, headings.length);
	}
	
	public void testWriteSimpleFieldByLabel() throws DataException {
		
		SimpleTextFieldsOut fields = new SimpleTextFieldsOut();
		
		TextLayout test = new TextLayout();
		test.setName("fruit");
		test.setLabel("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.setBinding(binding);

		DataWriter writer = test.writerFor(fields);
		
		assertEquals(false, writer.write("Apples"));

		String[] results = fields.values();
		
		assertEquals("Apples", results[0]);
		assertEquals(1, results.length);
		
		String[] headings = fields.headings();
		assertEquals("Fruit", headings[0]);
		assertEquals(1, headings.length);
	}
	
	public void testReadSimpleFieldsNoLabels() throws DataException {
		
		SimpleTextFieldsIn fields = new SimpleTextFieldsIn();
		fields.setValues(new String[] { "Apple" });
		
		TextLayout test = new TextLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.setBinding(binding);
		
		DataReader reader = test.readerFor(fields);
		
		Object result = reader.read();
		
		assertEquals("Apple", result);
		
		assertNull(reader.read());
	}
	
	public void testReadSimpleFieldsByLabel() throws DataException {
		
		SimpleTextFieldsIn fields = new SimpleTextFieldsIn();
		fields.setHeadings(new String[] { "Person", "Fruit" });
		
		fields.setValues(new String[] { "John", "Apple" });
		
		TextLayout test = new TextLayout();
		test.setLabel("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.setBinding(binding);
		
		DataReader reader = test.readerFor(fields);
		
		Object result = reader.read();
		
		assertEquals("Apple", result);
		
		assertNull(reader.read());
		
		fields.setValues(new String[] { "Foo", "Apple" });
		
	}
	
	public void testInputNoChildren() throws DataException {

		TextLayout test = new TextLayout();
		
		SimpleTextFieldsIn fields = new SimpleTextFieldsIn();
		
		fields.setHeadings(new String[] { "name" });
		fields.setValues(new String[] { "John" });

		test.setBinding(new DirectBinding());
		
		DataReader reader = test.readerFor(fields);
		
		String result = (String) reader.read();
				
		assertEquals("John", result);
		
		assertNull(reader.read());
		
		reader.close();
		
		fields.setValues(new String[] { "Jane" });
		
		reader = test.readerFor(fields);
				
		result = (String) reader.read();
		
		assertEquals("Jane", result);
		
		assertNull(reader.read());
		
		reader.close();
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
				"  <dido:text>" +
				"   <of>" +
				"    <dido:delimited delimiter='|'>" +
				"     <of>" +
				"      <dido:text name='fruit'/>" +
				"      <dido:text name='colour'/>" +
				"     </of>" +
				"    </dido:delimited>" +
				"   </of>" +
				"  </dido:text>" +
				" </of>" +
				"</dido:delimited>";
		
		Layout layout = (Layout) 
				OddjobTestHelper.createValueFromConfiguration(new XMLConfiguration("XML", xml));
	
		BeanBindingBean binding = new BeanBindingBean();
		binding.setArooaSession(new StandardArooaSession());
		binding.setType(new SimpleArooaClass(Fruit.class));
		
		layout.setBinding(binding);
		
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
