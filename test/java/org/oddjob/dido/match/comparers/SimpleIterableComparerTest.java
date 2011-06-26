package org.oddjob.dido.match.comparers;

import java.util.Arrays;

import junit.framework.TestCase;

public class SimpleIterableComparerTest extends TestCase {

	public void testCompareEqual() {
		
		SimpleIterableComparer test = new SimpleIterableComparer();
		
		test.setComparersByType(new DefaultComparersByType());
		
		Iterable<String> x = Arrays.asList("a", "b", "c");
		
		Iterable<String> y = Arrays.asList("b", "c", "a");
		
		MultiItemComparison comparison = test.compare(x, y);
		
		assertEquals(true, comparison.isEqual());
		assertEquals(3, comparison.getSame());
		assertEquals(0, comparison.getDifferent());		
		assertEquals(0, comparison.getXsMissing());
		assertEquals(0, comparison.getYsMissing());
	}
	
	public void testCompareOneDifferentEqual() {
		
		SimpleIterableComparer test = new SimpleIterableComparer();
		
		test.setComparersByType(new DefaultComparersByType());
		
		Iterable<String> x = Arrays.asList("a", "b", "c");
		
		Iterable<String> y = Arrays.asList("b", "c", "d");
		
		MultiItemComparison comparison = test.compare(x, y);
		
		assertEquals(false, comparison.isEqual());
		assertEquals(2, comparison.getSame());
		assertEquals(1, comparison.getDifferent());		
		assertEquals(0, comparison.getXsMissing());
		assertEquals(0, comparison.getYsMissing());
	}

	public void testCompareOneXMissing() {
		
		SimpleIterableComparer test = new SimpleIterableComparer();
		
		test.setComparersByType(new DefaultComparersByType());
		
		Iterable<String> x = Arrays.asList("b", "c");
		
		Iterable<String> y = Arrays.asList("b", "c", "a");
		
		MultiItemComparison comparison = test.compare(x, y);
		
		assertEquals(false, comparison.isEqual());
		assertEquals(2, comparison.getSame());
		assertEquals(0, comparison.getDifferent());		
		assertEquals(1, comparison.getXsMissing());
		assertEquals(0, comparison.getYsMissing());
	}

	public void testCompareOneYMissing() {
		
		SimpleIterableComparer test = new SimpleIterableComparer();
		
		test.setComparersByType(new DefaultComparersByType());
		
		Iterable<String> x = Arrays.asList("a", "b", "c");
		
		Iterable<String> y = Arrays.asList("b", "c");
		
		MultiItemComparison comparison = test.compare(x, y);
		
		assertEquals(false, comparison.isEqual());
		assertEquals(2, comparison.getSame());
		assertEquals(0, comparison.getDifferent());		
		assertEquals(0, comparison.getXsMissing());
		assertEquals(1, comparison.getYsMissing());
	}

}
