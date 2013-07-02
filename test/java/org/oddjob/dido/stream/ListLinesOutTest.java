package org.oddjob.dido.stream;

import junit.framework.TestCase;

import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.text.TextOut;

public class ListLinesOutTest extends TestCase {

	public void testWrite() throws UnsupportedeDataOutException {
		
		ListLinesOut test = new ListLinesOut();

		TextOut text = test.provideDataOut(TextOut.class);
		
		text.append("apples");
		
		assertEquals("apples", test.lastLine());
		
	}
}
