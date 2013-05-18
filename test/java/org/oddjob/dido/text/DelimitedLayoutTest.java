package org.oddjob.dido.text;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.MockDataOut;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.bio.ValueBinding;
import org.oddjob.dido.stream.LinesOut;

public class DelimitedLayoutTest extends TestCase {

	private class OurLinesOut extends MockDataOut
	implements LinesOut {
		
		private List<String> results = new ArrayList<String>();
		
		@Override
		public <T extends DataOut> T provide(Class<T> type)
				throws UnsupportedeDataOutException {
			if (LinesOut.class.isAssignableFrom(type)) {
				return type.cast(this);
			}
			else {
				throw new UnsupportedeDataOutException(getClass(), type);
			}
		}
		
		@Override
		public void writeLine(String text) throws DataException {
			results.add(text);
		}
	}
	
	public void testVerySimpleNoChild() throws DataException {
		
		OurLinesOut results = new OurLinesOut();
		
		ValueBinding binding = new ValueBinding();
		
		DelimitedLayout test = new DelimitedLayout();
		
		test.bind(binding);
		
		DataWriter writer = test.writerFor(results);
		
		writer.write(new String[] {"Apples", "red" });
		writer.write(new String[] {"Bananas", "yellow" });
		
		assertEquals("Apples,red", results.results.get(0));
		assertEquals("Bananas,yellow", results.results.get(1));
		assertEquals(2, results.results.size());
	}
	
	public void testWithHeadingsNoChild() throws DataException {
		
		OurLinesOut results = new OurLinesOut();
		
		ValueBinding binding = new ValueBinding();
		
		DelimitedLayout test = new DelimitedLayout();

		test.setWithHeadings(true);
		test.setHeadings(new String[] {"Fruit", "colour" });
		test.bind(binding);
		
		DataWriter writer = test.writerFor(results);
		
		writer.write(new String[] {"Apples", "red" });
		writer.write(new String[] {"Bananas", "yellow" });
		
		assertEquals("Fruit,colour", results.results.get(0));
		assertEquals("Apples,red", results.results.get(1));
		assertEquals("Bananas,yellow", results.results.get(2));
		assertEquals(3, results.results.size());
	}
}
