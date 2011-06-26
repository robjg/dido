package org.oddjob.dido.match.matchables;

import java.util.Arrays;

import org.oddjob.dido.match.matchables.MatchKey;
import org.oddjob.dido.match.matchables.SimpleMatchKey;

import junit.framework.TestCase;

public class SimpleMatchKeyTest extends TestCase {

	public void testDifferentOneComponent() {
		MatchKey key1 = new SimpleMatchKey(Arrays.asList("banana"));
		MatchKey key2 = new SimpleMatchKey(Arrays.asList("apple"));
		
		assertTrue(key1.compareTo(key2) > 0);
		assertTrue(key2.compareTo(key1) < 0);
		
		assertFalse(key1.equals(key2));
	}
	
	public void testDifferentTwoComponents() {
		MatchKey key1 = new SimpleMatchKey(Arrays.asList("banana", "banana"));
		MatchKey key2 = new SimpleMatchKey(Arrays.asList("banana", "apple"));
		
		assertTrue(key1.compareTo(key2) > 0);
		assertTrue(key2.compareTo(key1) < 0);
		
		assertFalse(key1.equals(key2));
	}

	public void testSameThreeComponents() {
		MatchKey key1 = new SimpleMatchKey(Arrays.asList(
				"orange", "banana", "apple"));
		MatchKey key2 = new SimpleMatchKey(Arrays.asList(
				"orange", "banana", "apple"));
		
		assertTrue(key1.compareTo(key2) == 0);
		assertTrue(key2.compareTo(key1) == 0);
		
		assertTrue(key1.equals(key2));
		assertEquals(key1.hashCode(), key2.hashCode());
	}
	
	public void testDifferentOneComponentNull() {
		MatchKey key1 = new SimpleMatchKey(Arrays.asList("banana", "banana", "apple" ));
		MatchKey key2 = new SimpleMatchKey(Arrays.asList("banana", null, "apple" ));
		
		assertTrue(key1.compareTo(key2) > 0);
		assertTrue(key2.compareTo(key1) < 0);
		
		assertFalse(key1.equals(key2));
	}
}
