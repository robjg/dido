package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;

public class OutgoingTextTest extends TestCase {

	class OurOutgoingText extends StringTextOut {
		@Override
		public boolean flush() throws DataException {
			return true;
		}
	}
	
	public void testDefault() throws DataException {

		StringTextOut test = new OurOutgoingText();
		
		test.write("Apple", 0, -1);
		
		assertEquals("Apple", test.toString());
	}

	public void testTruncate() throws DataException {

		StringTextOut test = new OurOutgoingText();
		
		test.write("Apples and Oranges", 0, 6);
		
		assertEquals("Apples", test.toString());
	}

	public void testFromStartFixed() throws DataException {

		StringTextOut test = new OurOutgoingText();
		
		test.write("Apple", 0, 12);
		
		assertEquals("Apple       ", test.toString());
	}

	public void testReplacingSection() throws DataException {

		StringTextOut test = new OurOutgoingText();
		
		test.write("Apples and Oranges", 0, -1);
		
		test.write("or", 7, 3);

		assertEquals("Apples or  Oranges", test.toString());
	}

	public void testReplacingExisting() throws DataException {

		StringTextOut test = new OurOutgoingText();
		
		test.write("Apples and Pears", 0, -1);
		
		test.write("Oranges", 11, -1);

		assertEquals("Apples and Oranges", test.toString());
	}

	public void testInsertTruncated() throws DataException {

		StringTextOut test = new OurOutgoingText();
		
		test.write("Apples and Oranges", 0, -1);
		
		test.write("or maybe", 7, 3);

		assertEquals("Apples or  Oranges", test.toString());
	}
	
	public void testManySections() throws DataException {

		StringTextOut test = new OurOutgoingText();
		
		test.write("Apples", 0, 10);
		test.write("Oranges", 10, 10);
		test.write("Pears", 20, 10);
		
		assertEquals("Apples    Oranges   Pears     ", test.toString());
	}
	
	public void testStartingPastStart() throws DataException {

		StringTextOut test = new OurOutgoingText();
		
		test.write("Apples", 5, -1);
		
		assertEquals("     Apples", test.toString());
	}
}
