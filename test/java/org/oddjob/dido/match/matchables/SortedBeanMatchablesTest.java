package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.match.Iterables;
import org.oddjob.dido.match.MatchDefinition;
import org.oddjob.dido.match.SimpleMatchDefinition;


public class SortedBeanMatchablesTest extends TestCase {

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
				new String[] { },
				new String[] { "colour" });
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);
		
		List<Fruit> fruits = new ArrayList<Fruit>();
		
		Fruit bean1 = new Fruit();
		bean1.setType("Apple");
		bean1.setColour("red");
		
		Fruit bean2 = new Fruit();
		bean2.setType("Apple");
		bean2.setColour("green");

		Fruit bean3 = new Fruit();
		bean3.setType("Banana");
		bean3.setColour("yellow");
		
		fruits.add(bean1);
		fruits.add(bean2);
		fruits.add(bean3);
		
		Iterable<MatchableGroup> test = new SortedBeanMatchables<Object>(
				fruits, factory);
		
		MatchableGroup[] groups = Iterables.toArray(
				test, MatchableGroup.class);		
		
		assertEquals(2, groups.length);
				
		Matchable[] matchables = Iterables.toArray(
				groups[0].getGroup(), Matchable.class);
				
		assertEquals(2, matchables.length);
		
		assertEquals(new SimpleMatchKey(Arrays.asList("Apple")), 
				matchables[0].getKey());
		
		Object[] others = Iterables.toArray(
				matchables[0].getOthers(), Object.class);
		
		assertEquals("red", others[0]);
	
		matchables = Iterables.toArray(
				groups[1].getGroup(), Matchable.class);
		
		assertEquals(new SimpleMatchKey(Arrays.asList("Banana")),
				matchables[0].getKey());		
	}

	private class MockFactory implements MatchableFactory<Object> {
		@Override
		public Matchable createMatchable(Object bean) {
			throw new RuntimeException("Unexpected!");
		}
	}
	
	public void testEmptyIterator() {
		
		final SortedBeanMatchables<Object> test = new SortedBeanMatchables<Object>(
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
