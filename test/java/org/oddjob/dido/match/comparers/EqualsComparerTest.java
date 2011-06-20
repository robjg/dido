package org.oddjob.dido.match.comparers;

import junit.framework.TestCase;

import org.oddjob.dido.match.Comparison;

public class EqualsComparerTest extends TestCase {

	public void testNotEquals() {
		
		EqualsComparer test = new EqualsComparer();
		
		Comparison comparison = test.compare("Apples", "Oranges");
		
		assertEquals(false, comparison.isEqual());
		
		assertEquals("Apples<>Oranges", comparison.getSummaryText());
	}
	
	public void testEquals() {
		
		EqualsComparer test = new EqualsComparer();
		
		Comparison comparison = test.compare("Apples", "Apples");
		
		assertEquals(true, comparison.isEqual());
		
		assertEquals("", comparison.getSummaryText());
	}
}
