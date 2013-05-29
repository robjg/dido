package org.oddjob.dido.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.bio.ValueBinding;
import org.oddjob.dido.stream.LinesLayout;
import org.oddjob.dido.stream.ListLinesOut;
import org.oddjob.dido.stream.OutputStreamOut;



public class DelimitedTest extends TestCase {

	public void testReadWtihNoChildren() throws DataException {
		
		TextIn dataIn = new StringTextIn("a,b,c");

		DelimitedLayout delimited = new DelimitedLayout();
		delimited.bind(new ValueBinding());
		
		DataReader reader = delimited.readerFor(dataIn);
		
		Object o = reader.read();
		
		String[] results = (String[]) o;
		
		assertEquals("a", results[0]);
		assertEquals("b", results[1]);
		assertEquals("c", results[2]);
		assertEquals(3, results.length);
	}
	
	public void testSimpleInWithUnNamedChildren() throws DataException {
		
		String data = "a,b,c";

		DelimitedLayout delimited = new DelimitedLayout();

		FieldLayout a = new FieldLayout();
		FieldLayout b = new FieldLayout();
		FieldLayout c = new FieldLayout();
				
		delimited.setOf(0, a);
		delimited.setOf(1, b);
		delimited.setOf(2, c);
		
		TextIn dataIn = new StringTextIn(data);
		
		DataReader reader = delimited.readerFor(dataIn);
		
		assertNull(reader.read());
		
		assertEquals(1, a.getColumn());
		assertEquals(2, b.getColumn());
		assertEquals(3, c.getColumn());
		
		assertEquals("a", a.getValue());
		assertEquals("b", b.getValue());
		assertEquals("c", c.getValue());

	}

	public void testSimpleInWithNamedChildren() throws DataException {
		
		String data = "a,b,c";

		DelimitedLayout delimited = new DelimitedLayout();
		delimited.setHeadings(new String[] { "fieldA", "fieldB", "fieldC" });
		delimited.setWithHeadings(false);
		
		FieldLayout a = new FieldLayout();
		a.setName("fieldA");
		FieldLayout b = new FieldLayout();
		b.setName("fieldB");
		FieldLayout c = new FieldLayout();
		c.setName("fieldC");
				
		delimited.setOf(0, c);
		delimited.setOf(1, b);
		delimited.setOf(2, a);		
		
		ArooaSession session = new StandardArooaSession();
		BeanBindingBean binding = new BeanBindingBean();
		binding.setArooaSession(session);
		
		delimited.bind(binding);

		DataReader reader = delimited.readerFor(new StringTextIn(data));
		
		Object result =  reader.read();
		
		assertNotNull(result);
		
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		assertEquals("a", accessor.getProperty(result, "fieldA"));
		assertEquals("b", accessor.getProperty(result, "fieldB"));
		assertEquals("c", accessor.getProperty(result, "fieldC"));
	}

	String EOL = System.getProperty("line.separator");
	
	public void testWriteNoChildrenTheory() throws DataException {
				
		DelimitedLayout test = new DelimitedLayout();
		test.bind(new ValueBinding());
		
		ListLinesOut dataOut = new ListLinesOut();
				
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write(new String[] { "a", "b", "c" });

		assertEquals("a,b,c", dataOut.getLines().get(0));
		
		writer.write(new String[] { "d", "e", "f" });
		
		assertEquals("d,e,f", dataOut.getLines().get(1));
		
		writer.write(new String[] { "h", "i", "j" });
		
		assertEquals("h,i,j", dataOut.getLines().get(2));

	}
	
	private class OurBinding implements Binding {
		
		private final String value;
		
		public OurBinding(String value) {
			this.value = value;
		}
		
		@Override
		public Object process(Layout node, DataIn dataIn, boolean revist)
				throws DataException {
			throw new RuntimeException("Unexpected.");
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean process(Object object, Layout node, DataOut dataOut)
				throws DataException {
			
			((ValueNode<String>) node).value(value);
			
			return false;
		}
		
		@Override
		public void reset() {
		}
	}
	
	public void testWriteDataAndHeadings() throws DataException, IOException {
		
		LinesLayout lines = new LinesLayout();

		DelimitedLayout delimited = new DelimitedLayout();
		delimited.setWithHeadings(true);

		FieldLayout a = new FieldLayout();
		a.setTitle("fieldA");
		
		FieldLayout b = new FieldLayout();
		b.setTitle("fieldB");
		
		FieldLayout c = new FieldLayout();
		c.setTitle("fieldC");
		
		delimited.setOf(0, a);
		delimited.setOf(1, b);
		delimited.setOf(2, c);		
		
		lines.setOf(0, delimited);
				
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		DataWriter writer = lines.writerFor(new OutputStreamOut(output));
		
		a.bind(new OurBinding("a"));
		b.bind(new OurBinding("b"));
		c.bind(new OurBinding("c"));
		
		writer.write(new Object());
		writer.write(new Object());
		
		output.close();
		
		String expected = 
			"fieldA,fieldB,fieldC" + EOL + 
			"a,b,c" + EOL +
			"a,b,c" + EOL;
		
		assertEquals(expected, output.toString());
	}

	public void testOutWtihNoChildren() throws DataException {
		
		DelimitedLayout delimited = new DelimitedLayout();

		delimited.bind(new ValueBinding());

		TextOut dataOut = new StringTextOut();
		
		DataWriter writer = delimited.writerFor(dataOut);
		
		writer.write(new String[] {"a", "b", "c" });
		
		String expected = "a,b,c";

		assertEquals(expected, dataOut.toValue(String.class));
	}
	
	public void testSimpleOutWithUnNamedChildren() throws DataException {
		
		DelimitedLayout delimited = new DelimitedLayout();

		FieldLayout a = new FieldLayout();
		delimited.setOf(0, a);
		
		FieldLayout b = new FieldLayout();
		delimited.setOf(1, b);
				
		FieldLayout c = new FieldLayout();
		delimited.setOf(2, c);
		
		a.bind(new OurBinding("a"));
		b.bind(new OurBinding("b"));
		c.bind(new OurBinding("c"));
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = delimited.writerFor(dataOut);
		
		writer.write(new Object());
		
		assertEquals("a,b,c", dataOut.getLines().get(0));
		
		a.bind(new OurBinding("d"));
		b.bind(new OurBinding("e"));
		c.bind(new OurBinding("f"));
		
		writer.write(new Object());
		
		assertEquals("d,e,f", dataOut.getLines().get(1));
	}
}
