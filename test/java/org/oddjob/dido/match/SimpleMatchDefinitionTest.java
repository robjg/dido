package org.oddjob.dido.match;

import java.util.Iterator;

import junit.framework.TestCase;

import org.oddjob.dido.match.MatchDefinition;
import org.oddjob.dido.match.SimpleMatchDefinition;


public class SimpleMatchDefinitionTest extends TestCase {

	public void testSimple() {
		MatchDefinition test = new SimpleMatchDefinition(
				new String[] { "id", "fruit"},
				new String[] { "colour", "quantity", "price"},
				new String[] { "country", "organic" } );
		
		Iterator<String> iter;
		
		iter = test.getKeyProperties().iterator();
		
		assertEquals("id", iter.next());
		assertEquals("fruit", iter.next());
		
		iter = test.getValueProperties().iterator();
		
		assertEquals("colour", iter.next());
		assertEquals("quantity", iter.next());
		assertEquals("price", iter.next());
		
		iter = test.getOtherProperties().iterator();
		
		assertEquals("country", iter.next());
		assertEquals("organic", iter.next());						
	}		
}
