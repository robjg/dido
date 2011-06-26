package org.oddjob.dido.match.matchables;

import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.dido.match.Iterables;

public class MatchableIterableTest extends TestCase {

	public void testIterableOfIntegers() {
		
		MatchableIterable<Integer> test = new MatchableIterable<Integer>(
				Arrays.asList("a", "b", "c"), 
				Arrays.asList(new Integer(1), new Integer(2), new Integer(3)),
				Arrays.asList(new Integer(7), new Integer(8), new Integer(9)));
	
		MatchableIterable.MatchableSet<Integer>[] sets = 
			Iterables.toArray(test, MatchableIterable.MatchableSet.class);

		assertEquals(3, sets.length);
		
		assertEquals("a", sets[0].getPropertyName());
		assertEquals(new Integer(1), sets[0].getValueX());
		assertEquals(new Integer(7), sets[0].getValueY());
		
		assertEquals("b", sets[1].getPropertyName());
		assertEquals(new Integer(2), sets[1].getValueX());
		assertEquals(new Integer(8), sets[1].getValueY());
		
		assertEquals("c", sets[2].getPropertyName());
		assertEquals(new Integer(3), sets[2].getValueX());
		assertEquals(new Integer(9), sets[2].getValueY());
	}
	
	public void testWhenXisNull() {
		
		MatchableIterable<Integer> test = new MatchableIterable<Integer>(
				Arrays.asList("a", "b", "c"), 
				null,
				Arrays.asList(new Integer(7), new Integer(8), new Integer(9)));
	
		MatchableIterable.MatchableSet<Integer>[] sets = 
			Iterables.toArray(test, MatchableIterable.MatchableSet.class);

		assertEquals(3, sets.length);
		
		assertEquals("a", sets[0].getPropertyName());
		assertEquals(null, sets[0].getValueX());
		assertEquals(new Integer(7), sets[0].getValueY());
		
		assertEquals("b", sets[1].getPropertyName());
		assertEquals(null, sets[1].getValueX());
		assertEquals(new Integer(8), sets[1].getValueY());
		
		assertEquals("c", sets[2].getPropertyName());
		assertEquals(null, sets[2].getValueX());
		assertEquals(new Integer(9), sets[2].getValueY());
	}
	
	public void testWhenYisNull() {
		
		MatchableIterable<Integer> test = new MatchableIterable<Integer>(
				Arrays.asList("a", "b", "c"), 
				Arrays.asList(new Integer(1), new Integer(2), new Integer(3)),
				null);
	
		MatchableIterable.MatchableSet<Integer>[] sets = 
			Iterables.toArray(test, MatchableIterable.MatchableSet.class);

		assertEquals(3, sets.length);
		
		assertEquals("a", sets[0].getPropertyName());
		assertEquals(new Integer(1), sets[0].getValueX());
		assertEquals(null, sets[0].getValueY());
		
		assertEquals("b", sets[1].getPropertyName());
		assertEquals(new Integer(2), sets[1].getValueX());
		assertEquals(null, sets[1].getValueY());
		
		assertEquals("c", sets[2].getPropertyName());
		assertEquals(new Integer(3), sets[2].getValueX());
		assertEquals(null, sets[2].getValueY());
	}
}
