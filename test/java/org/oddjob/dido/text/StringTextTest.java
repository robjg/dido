package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;

public class StringTextTest extends TestCase {
	
	public void testDefault() throws DataException {

		StringTextOut test = new StringTextOut();
		
		assertFalse(test.isWrittenTo());
		
		assertEquals(null, test.toText());
		
		test.write("Apple", 0, -1);
		
		assertEquals("Apple", test.toText());
	}

	public void testTruncate() throws DataException {

		StringTextOut test = new StringTextOut();
		
		test.write("Apples and Oranges", 0, 6);
		
		assertEquals("Apples", test.toText());
	}

	public void testFromStartFixed() throws DataException {

		StringTextOut test = new StringTextOut();
		
		test.write("Apple", 0, 12);
		
		assertEquals("Apple       ", test.toText());
	}

	public void testReplacingSection() throws DataException {

		StringTextOut test = new StringTextOut();
		
		test.write("Apples and Oranges", 0, -1);
		
		test.write("or", 7, 3);

		assertEquals("Apples or  Oranges", test.toText());
	}

	public void testReplacingExisting() throws DataException {

		StringTextOut test = new StringTextOut();
		
		test.write("Apples and Pears", 0, -1);
		
		test.write("Oranges", 11, -1);

		assertEquals("Apples and Oranges", test.toText());
	}

	public void testInsertTruncated() throws DataException {

		StringTextOut test = new StringTextOut();
		
		test.write("Apples and Oranges", 0, -1);
		
		test.write("or maybe", 7, 3);

		assertEquals("Apples or  Oranges", test.toText());
	}
	
	public void testManySections() throws DataException {

		StringTextOut test = new StringTextOut();
		
		test.write("Apples", 0, 10);
		test.write("Oranges", 10, 10);
		test.write("Pears", 20, 10);
		
		assertEquals("Apples    Oranges   Pears     ", test.toText());
	}
	
	public void testStartingPastStart() throws DataException {

		StringTextOut test = new StringTextOut();
		
		test.write("Apples", 5, -1);
		
		assertEquals("     Apples", test.toText());
	}
}
