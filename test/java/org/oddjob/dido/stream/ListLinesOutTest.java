package org.oddjob.dido.stream;

import junit.framework.TestCase;

import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.text.TextOut;

public class ListLinesOutTest extends TestCase {

	public void testWrite() throws UnsupportedDataOutException {
		
		ListLinesOut test = new ListLinesOut();

		TextOut text = test.provideDataOut(TextOut.class);
		
		text.append("apples");
		
		assertEquals("apples", test.lastLine());
		
	}
}
