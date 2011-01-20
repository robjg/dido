package org.oddjob.dido.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.io.DataWriter;
import org.oddjob.dido.stream.Lines;
import org.oddjob.dido.stream.LinesOut;
import org.oddjob.dido.stream.OutputStreamOut;



public class DelimitedTest extends TestCase {

	public void testInWtihNoChildren() throws DataException {
		
		String data = "a,b,c";

		Delimited delimited = new Delimited();

		WhereNextIn<FieldsIn> where = delimited.in(new StringTextIn(data));		
		
		assertNotNull(where);
		
		String[] results = delimited.getValue();
		
		assertEquals("a", results[0]);
		assertEquals("b", results[1]);
		assertEquals("c", results[2]);
		assertEquals(3, results.length);
	}
	
	public void testSimpleInWithUnNamedChildren() throws DataException {
		
		String data = "a,b,c";

		Delimited delimited = new Delimited();

		Field a = new Field();
				
		delimited.setIs(0, a);
		
		WhereNextIn<FieldsIn> where = delimited.in(new StringTextIn(data));		
		
		assertNull(delimited.getValue());
		
		assertEquals(1, a.getColumn());
		
		assertNotNull(where);
		
		FieldsIn fields = where.getChildData();
		
		assertEquals("a", fields.getColumn(1));
		assertEquals("b", fields.getColumn(2));
		assertEquals("c", fields.getColumn(3));

		DataNode<?, ?, ?, ?>[] children = where.getChildren();

		assertEquals(a, children[0]);
	}

	public void testSimpleInWithNamedChildren() throws DataException {
		
		String data = "a,b,c";

		Delimited delimited = new Delimited();
		delimited.setHeadings(new String[] { "fieldA", "fieldB", "fieldC" });
		delimited.setWithHeadings(true);
		
		Field a = new Field();
		a.setName("fieldA");
		Field b = new Field();
		b.setName("fieldB");
		Field c = new Field();
		c.setName("fieldC");
				
		delimited.setIs(0, c);
		delimited.setIs(1, b);
		delimited.setIs(2, a);		
		
		WhereNextIn<FieldsIn> where = delimited.in(new StringTextIn(data));		
		
		assertNull(delimited.getValue());
		
		assertNotNull(where);
	
		FieldsIn fields = where.getChildData();
		
		assertEquals("a", fields.getColumn(1));
		assertEquals("b", fields.getColumn(2));
		assertEquals("c", fields.getColumn(3));

		DataNode<?, ?, ?, ?>[] children = where.getChildren();

		assertEquals(a, children[2]);
		assertEquals(b, children[1]);
		assertEquals(c, children[0]);		
		
		c.in(fields);
		b.in(fields);
		a.in(fields);
		
		assertEquals("a", a.getValue());
		assertEquals("b", b.getValue());
		assertEquals("c", c.getValue());
	}

	String EOL = System.getProperty("line.separator");
	
	public void testWriteNoChildrenTheory() throws DataException {
		
		StringTextOut textOut = new StringTextOut() {
			@Override
			public boolean flush() throws DataException {
				clear();
				return true;
			}
		};
		
		Delimited test = new Delimited();
		
		test.setValue(new String[] { "a", "b", "c" });
		
		WhereNextOut<FieldsOut> where = test.out(textOut);
		assertNotNull(where);
		
		assertEquals("a,b,c", textOut.toString());
		textOut.flush();
		
		test.setValue(new String[] { "d", "e", "f" });
		
		test.out(textOut);
		
		assertEquals("d,e,f", textOut.toString());
		textOut.flush();
		
		test.setValue(new String[] { "h", "i", "j" });
		
		test.out(textOut);
		
		assertEquals("h,i,j", textOut.toString());
		textOut.flush();
	}
	
	
	public void testWriteDataAndHeadings() throws DataException, IOException {
		
		Lines lines = new Lines();

		Delimited delimited = new Delimited();
		delimited.setWithHeadings(true);

		Field a = new Field();
		a.setTitle("fieldA");
		
		Field b = new Field();
		b.setTitle("fieldB");
		
		Field c = new Field();
		c.setTitle("fieldC");
		
		delimited.setIs(0, a);
		delimited.setIs(1, b);
		delimited.setIs(2, c);		
		
		lines.setIs(0, delimited);
				
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		DataWriter<LinesOut> writer = new DataWriter<LinesOut>(lines, 
			new OutputStreamOut(output));
		
		a.setValue("a");
		b.setValue("b");
		c.setValue("c");
		
		writer.write(new Object());
		writer.write(new Object());
		writer.complete();
		
		output.close();
		
		String expected = 
			"fieldA,fieldB,fieldC" + EOL + 
			"a,b,c" + EOL +
			"a,b,c" + EOL;
		
		assertEquals(expected, output.toString());
	}

	public void testOutWtihNoChildren() throws DataException {
		
		Delimited delimited = new Delimited();

		final AtomicReference<String> ref = new AtomicReference<String>();
		
		delimited.setValue(new String[] {"a", "b", "c" });
		
		TextOut out = new StringTextOut() {
					@Override
					public boolean flush() throws DataException {
						ref.set(this.toString());
						return true;
					}
				};
		WhereNextOut<FieldsOut> where = delimited.out(out);		
		
		assertNotNull(where);
		assertNull(where.getChildData());
		assertNull(where.getChildren());
		
		out.flush();
		
		String expected = "a,b,c";

		assertEquals(expected, ref.get());
	}
	
	public void testSimpleOutWithUnNamedChildren() throws DataException {
		
		Delimited delimited = new Delimited();

		Field a = new Field();
				
		delimited.setIs(0, a);
		
		final AtomicReference<String> ref = new AtomicReference<String>();		
		
		TextOut out = new StringTextOut() {
					@Override
					public boolean flush() throws DataException {
						ref.set(this.toString());
						clear();
						return true;
					}
				};
				
		WhereNextOut<FieldsOut> where = delimited.out(out);		
				
		assertNotNull(where);
		
		FieldsOut fields = where.getChildData();
		
		fields.setColumn(1, "a");
		fields.setColumn(2, "b");
		fields.setColumn(3, "c");

		DataNode<?, ?, ?, ?>[] children = where.getChildren();

		assertEquals(a, children[0]);
		
		fields.flush();
		out.flush();
		
		assertEquals("a,b,c", ref.get());
		
		where = delimited.out(out);
		
		assertNotNull(where);
		
		fields = where.getChildData();
		
		fields.setColumn(1, "d");
		fields.setColumn(2, "e");
		fields.setColumn(3, "f");

		fields.flush();
		out.flush();
		
		assertEquals("d,e,f", ref.get());
	}
}
