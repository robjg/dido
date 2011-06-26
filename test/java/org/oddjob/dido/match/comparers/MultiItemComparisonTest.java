package org.oddjob.dido.match.comparers;

import junit.framework.TestCase;

public class MultiItemComparisonTest extends TestCase {

	public void testIsEqual() {
		
		MultiItemComparison test1 = new MultiItemComparison(0, 0, 0, 3);
		
		assertEquals(true, test1.isEqual());
		
		MultiItemComparison test2 = new MultiItemComparison(1, 0, 0, 3);
		
		assertEquals(false, test2.isEqual());
		
		MultiItemComparison test3 = new MultiItemComparison(0, 1, 0, 3);
		
		assertEquals(false, test3.isEqual());
		
		MultiItemComparison test4 = new MultiItemComparison(0, 0, 1, 3);
		
		assertEquals(false, test4.isEqual());		

	}
	
	public void testNumbers() {
		
		MultiItemComparison test = new MultiItemComparison(1, 2, 3, 4);
		
		assertEquals(1, test.getXsMissing());
		assertEquals(2, test.getYsMissing());
		assertEquals(3, test.getDifferent());
		assertEquals(4, test.getSame());	
	}
}
