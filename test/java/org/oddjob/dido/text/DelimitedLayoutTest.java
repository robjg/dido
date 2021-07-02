package org.oddjob.dido.text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.bio.ValueBinding;
import org.oddjob.dido.stream.IOStreamData;
import org.oddjob.dido.stream.LinesLayout;
import org.oddjob.dido.stream.ListLinesIn;
import org.oddjob.dido.stream.ListLinesOut;
import org.oddjob.state.ParentState;

public class DelimitedLayoutTest extends TestCase {
	
	String EOL = System.getProperty("line.separator");
	
	public void testWriteLinesOutNoChildren() throws DataException {
				
		DelimitedLayout test = new DelimitedLayout();
		test.setBinding(new DirectBinding());
		
		ListLinesOut dataOut = new ListLinesOut();
				
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write(new String[] { "a", "b", "c" });

		assertEquals("a,b,c", dataOut.getLines().get(0));
		
		writer.write(new String[] { "d", "e", "f" });
		
		assertEquals("d,e,f", dataOut.getLines().get(1));
		
		writer.write(new String[] { "h", "i", "j" });
		
		assertEquals("h,i,j", dataOut.getLines().get(2));

		writer.close();
		
		assertEquals(3, dataOut.getLines().size());
	}	
	
	public void testWriteDataAndHeadings() throws DataException, IOException {
		
		LinesLayout lines = new LinesLayout();

		DelimitedLayout delimited = new DelimitedLayout();
		delimited.setWithHeadings(true);

		TextLayout a = new TextLayout();
		a.setLabel("fieldA");
		
		TextLayout b = new TextLayout();
		b.setLabel("fieldB");
		
		TextLayout c = new TextLayout();
		c.setLabel("fieldC");
		
		delimited.setOf(0, a);
		delimited.setOf(1, b);
		delimited.setOf(2, c);		
		
		lines.setOf(0, delimited);
				
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IOStreamData ioData = new IOStreamData();
		ioData.setArooaSession(new StandardArooaSession());
		ioData.setOutput(new ArooaObject(output));
		
		DataWriter writer = lines.writerFor(ioData);
		
		ArooaSession session = new StandardArooaSession();
		
		ValueBinding bindingA = new ValueBinding();
		bindingA.setArooaSession(session);
		bindingA.setValue("a");
		
		ValueBinding bindingB = new ValueBinding();
		bindingB.setArooaSession(session);
		bindingB.setValue("b");
		
		ValueBinding bindingC = new ValueBinding();
		bindingC.setArooaSession(session);
		bindingC.setValue("c");
		
		a.setBinding(bindingA);
		b.setBinding(bindingB);
		c.setBinding(bindingC);
		
		writer.write(new Object());
		writer.write(new Object());
		
		output.close();
		
		String expected = 
			"fieldA,fieldB,fieldC" + EOL + 
			"a,b,c" + EOL +
			"a,b,c" + EOL;
		
		assertEquals(expected, output.toString());
	}

	public void testWriteTextOutWithNoChildren() throws DataException {
		
		DelimitedLayout test = new DelimitedLayout();

		test.setBinding(new DirectBinding());

		StringTextOut dataOut = new StringTextOut();
		
		DataWriter writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write(new String[] {"a", "b", "c" }));
		
		writer.close();

		assertEquals("a,b,c", dataOut.toText());
		
		dataOut = new StringTextOut();
		
		writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write(new String[] {"x", "y", "z" }));
		
		writer.close();
		
		assertEquals("x,y,z", dataOut.toText());
	}
	
	public void testWriteLinesOutWithHeadingsNoChildren() throws DataException {
		
		ListLinesOut results = new ListLinesOut();
		
		DirectBinding binding = new DirectBinding();
		
		DelimitedLayout test = new DelimitedLayout();

		test.setWithHeadings(true);
		test.setHeadings(new String[] {"Fruit", "colour" });
		test.setBinding(binding);
		
		DataWriter writer = test.writerFor(results);
		
		writer.write(new String[] {"Apples", "red" });
		writer.write(new String[] {"Bananas", "yellow" });
		
		writer.close();
		
		assertEquals("Fruit,colour", results.getLines().get(0));
		assertEquals("Apples,red", results.getLines().get(1));
		assertEquals("Bananas,yellow", results.getLines().get(2));
		assertEquals(3, results.getLines().size());
	}
	
	public void testWriteLinesOutWithUnNamedChildren() throws DataException {
		
		DelimitedLayout delimited = new DelimitedLayout();

		TextLayout a = new TextLayout();
		delimited.setOf(0, a);
		
		TextLayout b = new TextLayout();
		delimited.setOf(1, b);
				
		TextLayout c = new TextLayout();
		delimited.setOf(2, c);
		
		ArooaSession session = new StandardArooaSession();
		
		ValueBinding bindingA = new ValueBinding();
		bindingA.setArooaSession(session);
		bindingA.setValue("a");
		
		ValueBinding bindingB = new ValueBinding();
		bindingB.setArooaSession(session);
		bindingB.setValue("b");
		
		ValueBinding bindingC = new ValueBinding();
		bindingC.setArooaSession(session);
		bindingC.setValue("c");
		
		a.setBinding(bindingA);
		b.setBinding(bindingB);
		c.setBinding(bindingC);
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = delimited.writerFor(dataOut);
		
		writer.write(new Object());
		
		assertEquals("a,b,c", dataOut.getLines().get(0));
		
		bindingA.setValue("d");
		bindingB.setValue("e");
		bindingC.setValue("f");
		
		writer.write(new Object());
		
		assertEquals("d,e,f", dataOut.getLines().get(1));
		
		writer.close();
	}
	
	public void testReadListLinesNoChildren() throws DataException {
		
		ListLinesIn lines = new ListLinesIn(Arrays.asList(
				"Apples,red", "Bananas,yellow"));
		
		DirectBinding binding = new DirectBinding();
		
		DelimitedLayout test = new DelimitedLayout();
		
		test.setBinding(binding);
		
		DataReader reader = test.readerFor(lines);
		
		String[] result = (String[]) reader.read();
		
		assertEquals("Apples", result[0]);
		assertEquals("red", result[1]);
		assertEquals(2, result.length);
			
		result = (String[]) reader.read();
			
		assertEquals("Bananas", result[0]);
		assertEquals("yellow", result[1]);
		assertEquals(2, result.length);
		
		result = (String[]) reader.read();
				
		assertNull(result);
		
		reader.close();
	}
	
	public void testReadTextInNoChildren() throws DataException {
		
		TextIn dataIn = new StringTextIn("a,b,c");

		DelimitedLayout test = new DelimitedLayout();
		test.setBinding(new DirectBinding());
		
		DataReader reader = test.readerFor(dataIn);
		
		Object o = reader.read();
		
		String[] results = (String[]) o;
		
		assertEquals("a", results[0]);
		assertEquals("b", results[1]);
		assertEquals("c", results[2]);
		assertEquals(3, results.length);
		
		o = reader.read();
		
		assertEquals(null, o);
		
		reader.close();
	}
	
	public void testReadTextInWithUnNamedChildren() throws DataException {
		
		TextIn dataIn = new StringTextIn("a,b,c");
		
		DelimitedLayout test = new DelimitedLayout();

		TextLayout a = new TextLayout();
		TextLayout b = new TextLayout();
		TextLayout c = new TextLayout();
				
		test.setOf(0, a);
		test.setOf(1, b);
		test.setOf(2, c);
		
		DataReader reader = test.readerFor(dataIn);
		
		assertNull(reader.read());
		
		assertEquals("a", a.getValue());
		assertEquals("b", b.getValue());
		assertEquals("c", c.getValue());

		reader.close();
	}
	
	public void testReadListLinesWithNamedChildren() throws DataException {

		ListLinesIn linesIn = new ListLinesIn(Arrays.asList(
				"a,b,c", "s,t,u", "x,y,z"));
		
		DelimitedLayout test = new DelimitedLayout();
		test.setHeadings(new String[] { "fieldA", "fieldB", "fieldC" });
		test.setWithHeadings(false);
		
		TextLayout a = new TextLayout();
		a.setName("fieldA");
		a.setLabel("fieldA");
		
		TextLayout b = new TextLayout();
		b.setName("fieldB");
		b.setLabel("fieldB");
		
		TextLayout c = new TextLayout();
		c.setName("fieldC");
		c.setLabel("fieldC");
				
		test.setOf(0, c);
		test.setOf(1, b);
		test.setOf(2, a);		
		
		ArooaSession session = new StandardArooaSession();
		BeanBindingBean binding = new BeanBindingBean();
		binding.setArooaSession(session);
		
		test.setBinding(binding);

		DataReader reader = test.readerFor(linesIn);
		
		Object result =  reader.read();
		
		assertNotNull(result);
		
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		assertEquals("a", accessor.getProperty(result, "fieldA"));
		assertEquals("b", accessor.getProperty(result, "fieldB"));
		assertEquals("c", accessor.getProperty(result, "fieldC"));
		
		result =  reader.read();
		
		assertEquals("s", accessor.getProperty(result, "fieldA"));
		assertEquals("t", accessor.getProperty(result, "fieldB"));
		assertEquals("u", accessor.getProperty(result, "fieldC"));
		
		result =  reader.read();
		
		assertEquals("x", accessor.getProperty(result, "fieldA"));
		assertEquals("y", accessor.getProperty(result, "fieldB"));
		assertEquals("z", accessor.getProperty(result, "fieldC"));
		
		assertNull(reader.read());
		
		reader.close();
	}
	
	
	public void testSimpleReadWriteExample() throws ArooaPropertyException, ArooaConversionException {
		
		File file = new File(getClass().getResource(
				"DelimitedSimplestReadWrite.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String[] expected = lookup.lookup("read.data.input", String[].class);
		
		String[] result = lookup.lookup("write.data.output", String[].class);
		
		assertEquals(expected[0], result[0]);
		assertEquals(expected[1], result[1]);
		assertEquals(expected[2], result[2]);
		assertEquals(expected[3], result[3]);
		
		assertEquals(expected.length, result.length);
	}
	
	public static class Fruit {
		
		private String fruit;
		private int quantity;
		private double price;
		
		public String getFruit() {
			return fruit;
		}
		public void setFruit(String type) {
			this.fruit = type;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
	}
	
	public void testReadWriteByTypeExample() throws ArooaPropertyException, ArooaConversionException {
		
		File file = new File(getClass().getResource(
				"DelimitedReadWriteByType.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String[] expected = lookup.lookup("read.data.input", String[].class);
		
		String[] result = lookup.lookup("write.data.output", String[].class);
		
		assertEquals(expected[0], result[0]);
		assertEquals(expected[1], result[1]);
		assertEquals(expected[2], result[2]);
		assertEquals(expected[3], result[3]);
		
		assertEquals(expected.length, result.length);
	}
}
