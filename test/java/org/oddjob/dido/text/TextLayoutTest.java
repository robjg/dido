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

public class TextLayoutTest extends TestCase {

	public void testWriteSimple() throws DataException {
		
		StringTextOut dataOut = new StringTextOut();
		
		FixedWidthLayout fixed = new FixedWidthLayout();
		
		TextLayout test = new TextLayout();
		fixed.setOf(0, test);
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);

		DataWriter writer = fixed.writerFor(dataOut);
		
		assertEquals(false, writer.write("Apples"));
		
		writer.close();

		assertEquals("Apples", dataOut.toText());
		
		dataOut = new StringTextOut();
		
		writer = fixed.writerFor(dataOut);
		
		assertEquals(false, writer.write("Oranges"));
		
		writer.close();

		assertEquals("Oranges", dataOut.toText());		
	}
	
	public void testWriteSubstring() throws DataException {
		
		TextFieldsOut dataOut = new TextFieldsOut();
		
		TextLayout test = new TextLayout();
		test.setName("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		test.setIndex(5);
		test.setLength(3);

		DataWriter writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write("Apples"));

		assertEquals("    App", dataOut.getText());
	}
	
	public void testReadSimple() throws DataException {
		
		TextFieldsIn dataIn = new TextFieldsIn();
		dataIn.setText("Apples");
				
		TextLayout test = new TextLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(dataIn);
		
		Object result = reader.read();
		
		assertEquals("Apples", result);
		
		assertNull(reader.read());
	}
	
	public void testReadSubstring() throws DataException {
		
		TextFieldsIn dataIn = new TextFieldsIn();
		dataIn.setText("Apples");
		
		TextLayout test = new TextLayout();
		test.setName("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		test.setIndex(4);
		test.setLength(3);
		
		DataReader reader = test.readerFor(dataIn);
		
		Object result = reader.read();
		
		assertEquals("les", result);
		
		assertNull(reader.read());
	}
	
	public void testWriteSimpleField() throws DataException {
		
		TextFieldsOut textFieldsOut = new TextFieldsOut();
		assertEquals(false, textFieldsOut.isWrittenTo());
		
		TextLayout test = new TextLayout();
		test.setIndex(3);
		test.setLength(6);
		
		DirectBinding binding = new DirectBinding();
		test.bind(binding);
		
		DataWriter writer = test.writerFor(textFieldsOut);
		
		assertEquals(false, writer.write("Apples and Pears"));
		
		writer.close();
		
		assertEquals(true, textFieldsOut.isWrittenTo());
		assertEquals("  Apples", textFieldsOut.getText());
		
	}
	
	public void testReadSimpleField() throws DataException {
		
		TextFieldsIn textFieldsIn = new TextFieldsIn();
		textFieldsIn.setText("  Apples and Pears");
		
		TextLayout test = new TextLayout();
		test.setIndex(3);
		test.setLength(6);
		
		DirectBinding binding = new DirectBinding();
		test.bind(binding);
		
		DataReader reader = test.readerFor(textFieldsIn);
		
		assertEquals("Apples", reader.read());
		
		assertEquals(null, reader.read());
		
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
				"<dido:fixed xmlns:dido='oddjob:dido'>" +
				" <of>" +
				"  <dido:text>" +
				"   <of>" +
				"    <dido:fixed>" +
				"     <of>" +
				"      <dido:text index='1' name='fruit'/>" +
				"      <dido:text index='11' name='colour'/>" +
				"     </of>" +
				"    </dido:fixed>" +
				"   </of>" +
				"  </dido:text>" +
				" </of>" +
				"</dido:fixed>";
		
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
		
		assertEquals("apples    green", dataOut.getLines().get(0));
		assertEquals("plums     red", dataOut.getLines().get(1));
		assertEquals("bananas   yellow", dataOut.getLines().get(2));
		assertEquals(3, dataOut.getLines().size());
	}

}
