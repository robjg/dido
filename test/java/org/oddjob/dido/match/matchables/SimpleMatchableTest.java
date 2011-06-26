package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.match.Iterables;

import junit.framework.TestCase;


public class SimpleMatchableTest extends TestCase {
	
	public void testKeys() {
		
		List<Comparable<?>> keys1 = new ArrayList<Comparable<?>>();
		keys1.add("Apple");
		keys1.add(new Integer(2));
		
		SimpleMatchable test1  = new SimpleMatchable(
				keys1, null, null);
		
		List<Comparable<?>> keys2 = new ArrayList<Comparable<?>>();
		
		keys2.add("Banana");
		keys2.add(new Integer(1));
		
		SimpleMatchable test2  = new SimpleMatchable(
				keys2, null, null);
		
		assertTrue(test1.getKey().compareTo(test2.getKey()) < 0);
		
		assertTrue(test2.getKey().compareTo(test1.getKey()) > 0);
	}

	public void testValues() {
		
		List<Object> values = new ArrayList<Object>();
		values.add("Apple");
		values.add(new Integer(2));
		
		SimpleMatchable test1 = new SimpleMatchable(
				new ArrayList<Comparable<?>>(), values, null);
		
		Object[] results = Iterables.toArray(test1.getValues(), Object.class);
		assertEquals("Apple", results[0]);
		assertEquals(new Integer(2), results[1]);
				
		SimpleMatchable test2 = new SimpleMatchable(
				new ArrayList<Comparable<?>>(), null, null);
		
		assertEquals(0, test1.getKey().compareTo(test2.getKey()));
	}
	
	public void testOthers() {
		
		List<Object> others = new ArrayList<Object>();
		others.add("Apple");
		others.add(new Integer(2));
		
		SimpleMatchable test1 = new SimpleMatchable(
				null, null, others);
		
		Object[] results = Iterables.toArray(test1.getOthers(), Object.class);
		assertEquals("Apple", results[0]);
		assertEquals(new Integer(2), results[1]);
				
	}
}
