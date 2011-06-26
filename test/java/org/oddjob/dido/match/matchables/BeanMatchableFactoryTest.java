package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.match.Iterables;
import org.oddjob.dido.match.MatchDefinition;
import org.oddjob.dido.match.SimpleMatchDefinition;

public class BeanMatchableFactoryTest extends TestCase {

	public static class Snack {
		
		private String fruit;
		
		private String colour;

		public String getFruit() {
			return fruit;
		}

		public void setFruit(String fruit) {
			this.fruit = fruit;
		}

		public String getColour() {
			return colour;
		}

		public void setColour(String colour) {
			this.colour = colour;
		}
	}
		
	@SuppressWarnings("unchecked")
	public void testCreateKeys() {
		
		MatchDefinition definition = new SimpleMatchDefinition(
				new String[] { "fruit", "colour" }, 
				null,
				null);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);
		
		Snack snack = new Snack();
		
		Matchable result;
		Object[] values;
		
		
		result = factory.createMatchable(snack);
		
		assertEquals(new SimpleMatchKey(Arrays.asList(
				(Comparable<?>) null, (Comparable<?>) null)),
				result.getKey());

		values = Iterables.toArray(result.getKeys(), Object.class);
		
		assertEquals(2, values.length);
		
		assertEquals(null, values[0]);
		assertEquals(null, values[1]);
		
		snack.setFruit("apple");
		snack.setColour("red");
		
		result = factory.createMatchable(snack);
		
		assertEquals(new SimpleMatchKey(Arrays.asList("apple", "red")),
				result.getKey());

		values = Iterables.toArray(result.getKeys(), Object.class);
		
		assertEquals(2, values.length);
		
		assertEquals("apple", values[0]);
		assertEquals("red", values[1]);
		
		MatchableMetaData metaData = result.getMetaData();
		
		assertEquals(String.class, metaData.getPropertyType("fruit"));
	}
	
	public void testCreateValues() {
		
		MatchDefinition definition = new SimpleMatchDefinition(
				null,
				new String[] { "fruit", "colour" }, 
				null);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);
		
		Snack snack = new Snack();
		
		Matchable result;
		Object[] values;
		
		
		result = factory.createMatchable(snack);
		
		assertEquals(new SimpleMatchKey(new ArrayList<Comparable<Object>>()),
				result.getKey());

		values = Iterables.toArray(result.getValues(), Object.class);
		
		assertEquals(2, values.length);
		
		assertEquals(null, values[0]);
		assertEquals(null, values[1]);
		
		snack.setFruit("apple");
		snack.setColour("red");
		
		result = factory.createMatchable(snack);
		
		values = Iterables.toArray(result.getValues(), Object.class);
		
		assertEquals(2, values.length);
		
		assertEquals("apple", values[0]);
		assertEquals("red", values[1]);
	}
	
	public void testCreateOthers() {
		
		MatchDefinition definition = new SimpleMatchDefinition(
				null,
				null,
				new String[] { "fruit", "colour" }
				);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);
		
		Snack snack = new Snack();
		
		Matchable result;
		Object[] values;
		
		
		result = factory.createMatchable(snack);
		
		assertEquals(new SimpleMatchKey(new ArrayList<Comparable<Object>>()),
				result.getKey());

		values = Iterables.toArray(result.getOthers(), Object.class);
		
		assertEquals(2, values.length);
		
		assertEquals(null, values[0]);
		assertEquals(null, values[1]);
		
		snack.setFruit("apple");
		snack.setColour("red");
		
		result = factory.createMatchable(snack);
		
		values = Iterables.toArray(result.getOthers(), Object.class);
		
		assertEquals(2, values.length);
		
		assertEquals("apple", values[0]);
		assertEquals("red", values[1]);
		
	}
	
	public static class ThingWithPrimitive {
		
		public int getInt() {
			return 2;
		}
	}
	
	public void testPrimativeType() {
		
		
		MatchDefinition definition = new SimpleMatchDefinition(
				null,
				new String[] { "int" },
				null
				);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);

		
		ThingWithPrimitive thing = new ThingWithPrimitive();
		
		Matchable result = factory.createMatchable(thing);
		Object[] values = Iterables.toArray(result.getValues(), Object.class);
		
		assertEquals(2, values[0]);
		
		assertEquals(Integer.class, result.getMetaData().getPropertyType("int"));
	}	
}
