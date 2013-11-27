package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.OddjobTestHelper;
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

	public void testWriteSingleTextFieldNoWidth() throws DataException {
		
		FixedWidthTextFieldsOut dataOut = new FixedWidthTextFieldsOut();
		
		TextLayout test = new TextLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.setBinding(binding);

		DataWriter writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write("Apples"));
		
		writer.close();

		assertEquals("Apples", dataOut.getText());
		
		dataOut.clear();
		
		writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write("Oranges"));
		
		writer.close();

		assertEquals("Oranges", dataOut.getText());		
	}
	
	public void testWriteSingleTextFieldAsSubstring() throws DataException {
		
		FixedWidthTextFieldsOut dataOut = new FixedWidthTextFieldsOut();
		
		TextLayout test = new TextLayout();
		test.setName("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.setBinding(binding);
		test.setIndex(5);
		test.setLength(3);

		DataWriter writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write("Apples"));

		assertEquals("    App", dataOut.getText());
	}
	
	public void testReadSingleTextFieldNoWidth() throws DataException {
		
		FixedWidthTextFieldsIn dataIn = new FixedWidthTextFieldsIn();
		dataIn.setText("Apples");
				
		TextLayout test = new TextLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.setBinding(binding);
		
		DataReader reader = test.readerFor(dataIn);
		
		Object result = reader.read();
		
		assertEquals("Apples", result);
		assertEquals("Apples", test.getValue());
		
		assertNull(reader.read());
		assertEquals("Apples", test.getValue());
		
		reader.close();
		
		dataIn.setText("Oranges");
		
		reader = test.readerFor(dataIn);
		
		result = reader.read();
		
		assertEquals("Oranges", result);
		assertEquals("Oranges", test.getValue());
		
		assertNull(reader.read());
		
		reader.close();
	}
	
	public void testReadSingleTextFieldAsSubstring() throws DataException {
		
		FixedWidthTextFieldsIn dataIn = new FixedWidthTextFieldsIn();
		dataIn.setText("Apples");
		
		TextLayout test = new TextLayout();
		test.setName("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.setBinding(binding);
		test.setIndex(4);
		test.setLength(3);
		
		DataReader reader = test.readerFor(dataIn);
		
		Object result = reader.read();
		
		assertEquals("les", result);
		
		assertNull(reader.read());
	}
	
	public void testWriteSimpleField() throws DataException {
		
		FixedWidthTextFieldsOut textFieldsOut = new FixedWidthTextFieldsOut();
		assertEquals(false, textFieldsOut.isWrittenTo());
		
		TextLayout test = new TextLayout();
		test.setIndex(3);
		test.setLength(6);
		
		DirectBinding binding = new DirectBinding();
		test.setBinding(binding);
		
		DataWriter writer = test.writerFor(textFieldsOut);
		
		assertEquals(false, writer.write("Apples and Pears"));
		
		writer.close();
		
		assertEquals(true, textFieldsOut.isWrittenTo());
		assertEquals("  Apples", textFieldsOut.getText());
		
	}
	
	public void testReadSimpleField() throws DataException {
		
		FixedWidthTextFieldsIn textFieldsIn = new FixedWidthTextFieldsIn();
		textFieldsIn.setText("  Apples and Pears");
		
		TextLayout test = new TextLayout();
		test.setIndex(3);
		test.setLength(6);
		
		DirectBinding binding = new DirectBinding();
		test.setBinding(binding);
		
		DataReader reader = test.readerFor(textFieldsIn);
		
		assertEquals("Apples", reader.read());
		
		assertEquals(null, reader.read());
		
		reader.close();
		
	}
	
	public void testReadWithFixedWidthChild() throws DataException {

		TextLayout test = new TextLayout();
		test.setRaw(true);
		
		FixedWidthLayout fixedWidthLayout = new FixedWidthLayout();
		test.setOf(0, fixedWidthLayout);
		
		TextLayout textChild1 = new TextLayout();
		textChild1.setIndex(1);
		textChild1.setLength(5);
		
 		TextLayout textChild2 = new TextLayout();
		textChild2.setIndex(6);
		textChild2.setLength(10);
		textChild2.setRaw(true);
		
		fixedWidthLayout.setOf(0, textChild1);
		fixedWidthLayout.setOf(1, textChild2);

		FixedWidthTextFieldsIn text = new FixedWidthTextFieldsIn();
		text.setText("Big  Cheese  ");

		DataReader reader = test.readerFor(text);

		String next = (String) reader.read();

		assertNull(next);

		assertEquals("Big", textChild1.getValue());
		assertEquals("Cheese  ", textChild2.getValue());
	}	
	
	public void testWriteWithFixedWidthChild() throws DataException {

		TextLayout test = new TextLayout();
		
		FixedWidthLayout fixedWidthLayout = new FixedWidthLayout();
		test.setOf(0, fixedWidthLayout);
		
		TextLayout textChild1 = new TextLayout();
		textChild1.setIndex(1);
		textChild1.setLength(5);
		textChild1.setRaw(true);
		
		TextLayout textChild2 = new TextLayout();
		textChild2.setIndex(6);
		textChild2.setLength(10);

		fixedWidthLayout.setOf(0, textChild1);
		fixedWidthLayout.setOf(1, textChild2);
		
		textChild1.setBinding(new DirectBinding());
		textChild2.setBinding(new DirectBinding());
		
		FixedWidthTextFieldsOut dataOut = new FixedWidthTextFieldsOut();
	
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write("Big");
				
		assertEquals("Big  Big       ", dataOut.getText());
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
		
		assertEquals("apples    green", dataOut.getLines().get(0));
		assertEquals("plums     red", dataOut.getLines().get(1));
		assertEquals("bananas   yellow", dataOut.getLines().get(2));
		assertEquals(3, dataOut.getLines().size());
	}

}
