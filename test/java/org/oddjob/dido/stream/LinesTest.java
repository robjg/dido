package org.oddjob.dido.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.MockDataNode;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextOut;

public class LinesTest extends TestCase {

	String EOL = System.getProperty("line.separator");
	
	public void testReadThreeLines() throws DataException {
		
		String lines =
			"apples" + EOL +
			"oranges" + EOL +
			"bananas" + EOL;
		
		InputStream input = new ByteArrayInputStream(lines.getBytes());

		LinesIn data = new StreamLinesIn(input);
		
		Lines test = new Lines();
		
		WhereNextIn<TextIn> where1 = test.in(data);
		
		assertEquals("apples", test.getValue());

		assertNotNull(where1);		
		assertNull(where1.getChildData());
		assertNull(where1.getChildren());		
		
		WhereNextIn<TextIn> where2 = test.in(data);
		
		assertEquals("oranges", test.getValue());

		assertNotNull(where2);		
		assertNull(where2.getChildData());
		assertNull(where2.getChildren());		
		
		WhereNextIn<TextIn> where3 = test.in(data);
		
		assertEquals("bananas", test.getValue());

		assertNotNull(where3);		
		assertNull(where3.getChildData());
		assertNull(where3.getChildren());		
	}
	
	public void testReadLinesWithChildren() throws DataException {
		
		String lines =
			"apples" + EOL +
			"oranges" + EOL +
			"bananas" + EOL;
		
		InputStream input = new ByteArrayInputStream(lines.getBytes());

		LinesIn data = new StreamLinesIn(input);
				
		Lines test = new Lines();
		
		DataNode<TextIn, ?, TextOut, ?> child = 
			new MockDataNode<TextIn, StreamIn, TextOut, StreamOut>();
		
		test.setIs(0, child);
		
		WhereNextIn<TextIn> where1 = test.in(data);
		
		assertNull(test.getValue());

		assertNotNull(where1);
		assertSame(child, where1.getChildren()[0]);
		assertEquals("apples", where1.getChildData().getText());
		
		WhereNextIn<TextIn> where2 = test.in(data);
		
		assertNull(test.getValue());

		assertNotNull(where2);
		assertSame(child, where2.getChildren()[0]);
		assertEquals("oranges", where2.getChildData().getText());
		
		WhereNextIn<TextIn> where3 = test.in(data);
		
		assertNull(test.getValue());

		assertNotNull(where3);
		assertSame(child, where3.getChildren()[0]);
		assertEquals("bananas", where3.getChildData().getText());
	}
	
	public void testEmptyInputSteam() throws DataException {
		
		InputStream input = new ByteArrayInputStream(new byte[0]);

		LinesIn data = new StreamLinesIn(input);
				
		Lines test = new Lines();
		
		WhereNextIn<TextIn> where = test.in(data);
		
		assertNull(test.getValue());

		assertNull(where);
	}
	
	public void testWriteThreeLines() throws DataException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		StreamLinesOut data = new StreamLinesOut(output);
		
		Lines test = new Lines();
		
		test.setValue("apples");

		WhereNextOut<TextOut> where1 = test.out(data);
		
		assertNotNull(where1);		
		assertNull(where1.getChildData());
		assertNull(where1.getChildren());		
		
		test.setValue("oranges");

		WhereNextOut<TextOut> where2 = test.out(data);
		
		assertNotNull(where2);		
		assertNull(where2.getChildData());
		assertNull(where2.getChildren());		
		
		test.setValue("bananas");

		WhereNextOut<TextOut> where3 = test.out(data);
		
		assertNotNull(where3);		
		assertNull(where3.getChildData());
		assertNull(where3.getChildren());		
		
		String lines =
			"apples" + EOL +
			"oranges" + EOL +
			"bananas" + EOL;
	
		assertEquals(lines, new String(output.toByteArray()));
	}

	public void testWriteLinesWithChildren() throws DataException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		StreamLinesOut data = new StreamLinesOut(output);
		
		Lines test = new Lines();
		
		DataNode<TextIn, ?, TextOut, ?> child = 
			new MockDataNode<TextIn, StreamIn, TextOut, StreamOut>();
		
		test.setIs(0, child);
				
		WhereNextOut<TextOut> where1 = test.out(data);
		
		assertNull(test.getValue());

		assertNotNull(where1);
		assertSame(child, where1.getChildren()[0]);
		
		where1.getChildData().append("apples");
		where1.getChildData().flush();
		
		WhereNextOut<TextOut> where2 = test.out(data);
		
		assertNull(test.getValue());

		assertNotNull(where2);
		assertSame(child, where2.getChildren()[0]);
		
		where2.getChildData().append("oranges");
		where2.getChildData().flush();

		WhereNextOut<TextOut> where3 = test.out(data);
		
		assertNull(test.getValue());

		assertNotNull(where3);
		assertSame(child, where3.getChildren()[0]);
		
		where3.getChildData().append("bananas");
		where3.getChildData().flush();
		
		String lines =
			"apples" + EOL +
			"oranges" + EOL +
			"bananas" + EOL;
	
		assertEquals(lines, new String(output.toByteArray()));
	}

}
