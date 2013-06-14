package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.stream.ListLinesOut;

public class DelimitedLayoutTest extends TestCase {
	
	public void testVerySimpleNoChild() throws DataException {
		
		ListLinesOut results = new ListLinesOut();
		
		DirectBinding binding = new DirectBinding();
		
		DelimitedLayout test = new DelimitedLayout();
		
		test.bind(binding);
		
		DataWriter writer = test.writerFor(results);
		
		writer.write(new String[] {"Apples", "red" });
		writer.write(new String[] {"Bananas", "yellow" });
		
		assertEquals("Apples,red", results.getLines().get(0));
		assertEquals("Bananas,yellow", results.getLines().get(1));
		assertEquals(2, results.getLines().size());
	}
	
	public void testWithHeadingsNoChild() throws DataException {
		
		ListLinesOut results = new ListLinesOut();
		
		DirectBinding binding = new DirectBinding();
		
		DelimitedLayout test = new DelimitedLayout();

		test.setWithHeadings(true);
		test.setHeadings(new String[] {"Fruit", "colour" });
		test.bind(binding);
		
		DataWriter writer = test.writerFor(results);
		
		writer.write(new String[] {"Apples", "red" });
		writer.write(new String[] {"Bananas", "yellow" });
		
		assertEquals("Fruit,colour", results.getLines().get(0));
		assertEquals("Apples,red", results.getLines().get(1));
		assertEquals("Bananas,yellow", results.getLines().get(2));
		assertEquals(3, results.getLines().size());
	}
}
