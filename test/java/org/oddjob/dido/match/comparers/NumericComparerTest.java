package org.oddjob.dido.match.comparers;

import junit.framework.TestCase;

public class NumericComparerTest extends TestCase {

	
	public void testNoTolerances() {
		
		NumericComparer test = new NumericComparer();
		
		NumericComparison comparison = 
			test.compare(new Double(2.0), new Double(2.0));
		
		assertEquals(true, comparison.isEqual());
		
		comparison = 
			test.compare(new Double(200.0), new Double(190));
		
		assertEquals(false, comparison.isEqual());
		assertEquals(-10, comparison.getDelta(), 0.001);
		assertEquals(-5, comparison.getPercentage(), 0.001);
		assertEquals("-10.0 (-5.0%)", comparison.getSummaryText());
		
		comparison = 
			test.compare(new Double(200.0), new Double(250));
		
		assertEquals(false, comparison.isEqual());
		assertEquals(50, comparison.getDelta(), 0.001);
		assertEquals(25, comparison.getPercentage(), 0.001);
		assertEquals("50.0 (25.0%)", comparison.getSummaryText());

	}

	public void testOutsideNumericTolerance() {
		
		NumericComparer test = new NumericComparer();
		test.setDeltaTolerance(0.1);
		
		NumericComparison comparison = 
			test.compare(new Double(2.0), new Double(2.11));
		
		assertEquals(false, comparison.isEqual());
		assertEquals(0.11, comparison.getDelta(), 0.0001);
		assertEquals(5.5, comparison.getPercentage(), 0.0001);
		
		test.setDeltaTolerance(2);
		
		comparison = 
			test.compare(new Double(200.0), new Double(190.0));
		
		assertEquals(false, comparison.isEqual());		
		assertEquals(-10.0, comparison.getDelta(), 0.0001);
		assertEquals(-5.0, comparison.getPercentage(), 0.0001);
	}
	
	
	public void testOutsidePercentageTolerence() {
		
		NumericComparer test = new NumericComparer();
		test.setPercentageTolerance(5);
		test.setPercentageFormat("###");
		test.setDeltaFormat("##");
		
		NumericComparison comparison;
		
		comparison = test.compare(new Integer(200), new Integer(190));
		
		assertEquals(false, comparison.isEqual());
		assertEquals(-10.0, comparison.getDelta(), 0.0001);
		assertEquals(-5.0, comparison.getPercentage(), 0.0001);
		assertEquals("-10 (-5%)", comparison.getSummaryText());
		
		test.setDeltaTolerance(20);
		
		comparison = test.compare(new Integer(200), new Integer(190));
		
		assertEquals(true, comparison.isEqual());
	}
}
