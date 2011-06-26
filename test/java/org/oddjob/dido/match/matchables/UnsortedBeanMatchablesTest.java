package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.match.Iterables;
import org.oddjob.dido.match.MatchDefinition;
import org.oddjob.dido.match.SimpleMatchDefinition;

public class UnsortedBeanMatchablesTest extends TestCase {

	public static class Fruit {
		
		private String type;
		
		private String colour;
		
		public String getType() {
			return type;
		}
		
		public void setType(String make) {
			this.type = make;
		}
		
		public String getColour() {
			return colour;
		}
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
		
	
	public void testIterating() {
		
		MatchDefinition definition = new SimpleMatchDefinition(
				new String[] { "type" }, 
				new String[] { "colour" },
				null);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);
		
		List<Fruit> fruit = new ArrayList<Fruit>();
		
		Fruit bean1 = new Fruit();
		bean1.setType("banana");
		bean1.setColour("yellow");
		
		fruit.add(bean1);
		
		Fruit bean2 = new Fruit();
		bean2.setType("apple");
		bean2.setColour("red");
		
		fruit.add(bean2);
		
		Fruit bean3 = new Fruit();
		bean3.setType("apple");
		bean3.setColour("green");
		
		fruit.add(bean3);
		
		UnsortedBeanMatchables<Object> test = new UnsortedBeanMatchables<Object>(				
				fruit, factory);

		MatchableGroup[] groups = Iterables.toArray(
				test, MatchableGroup.class);
		
		assertEquals(2, groups.length);
		
		Matchable[] matchables = Iterables.toArray(groups[0].getGroup(), 
				Matchable.class);
		
		assertEquals(2, matchables.length);

		Object[] keys;
		Object[] values;
		
		keys = Iterables.toArray(matchables[0].getKeys(), 
				Object.class);

		assertEquals("apple", keys[0]);
		
		values = Iterables.toArray(matchables[0].getValues(), 
				Object.class);
		
		assertEquals("red", values[0]);
		
		
		keys = Iterables.toArray(matchables[1].getKeys(), 
				Object.class);

		assertEquals("apple", keys[0]);
		
		values = Iterables.toArray(matchables[1].getValues(), 
				Object.class);
		
		assertEquals("green", values[0]);
		
	}
	
	private class MockFactory implements MatchableFactory<Object> {
		@Override
		public Matchable createMatchable(Object bean) {
			throw new RuntimeException("Unexpected!");
		}
	}
	
	public void testEmptyIterator() {
		
		final UnsortedBeanMatchables<Object> test = 
			new UnsortedBeanMatchables<Object>(
				new ArrayList<Fruit>(), new MockFactory());
		
		Iterator<MatchableGroup> iter = test.iterator();
		
		assertFalse(iter.hasNext());
		
		try {
			assertNull(iter.next());
			fail("Exception expected.");
		}
		catch (NoSuchElementException e) {
			// expected.
		}
	}
		
}
