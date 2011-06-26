package org.oddjob.dido.match.comparers;

import org.oddjob.dido.match.Comparer;

import junit.framework.TestCase;

public class DefaultComparersByTypeTest extends TestCase {

	public void testFindByType() {
		
		DefaultComparersByType test = new DefaultComparersByType();
		
		Comparer<Number> numberComparer = test.comparerFor(Number.class);
		assertEquals(Number.class, numberComparer.getType());
		
		Comparer<Object> objectComparer = test.comparerFor(Object.class);
		assertEquals(Object.class, objectComparer.getType());
		
		Comparer<String[]> arrayComparer = test.comparerFor(String[].class);
		assertEquals(Object[].class, arrayComparer.getType());
	}
}
