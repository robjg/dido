package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.match.Iterables;
import org.oddjob.dido.match.MatchDefinition;
import org.oddjob.dido.match.SimpleMatchDefinition;
import org.oddjob.dido.match.beans.ComparersByPropertyOrType;
import org.oddjob.dido.match.comparers.DefaultComparersByType;
import org.oddjob.dido.match.matchables.BeanMatchableFactory;
import org.oddjob.dido.match.matchables.Matchable;
import org.oddjob.dido.match.matchables.MatchableComparison;
import org.oddjob.dido.match.matchables.MatchableGroup;
import org.oddjob.dido.match.matchables.MatchableMatchProcessor;
import org.oddjob.dido.match.matchables.OrderedMatchablesComparer;
import org.oddjob.dido.match.matchables.UnsortedBeanMatchables;

public class OrderedMatchablesComparerTest extends TestCase {
	
	public static class Fruit {
		
		private String type;
		
		private int quantity;
				
		public Fruit(String type, int qty) {
			this.type = type;
			this.quantity = qty;
		}
		
		public String getType() {
			return type;
		}
		
		public int getQuantity() {
			return quantity;
		}
	}
	
	private class Results implements MatchableMatchProcessor {
		
		List<Object[]> xsMissing = 
			new ArrayList<Object[]>();
		
		List<Object[]> ysMissing = 
			new ArrayList<Object[]>();
		
		List<Object[]> matchedKeys = new ArrayList<Object[]>();
		
		List<MatchableComparison> comparisons = 
			new ArrayList<MatchableComparison>();
		
		@Override
		public void matched(Matchable x, Matchable y, 
				MatchableComparison comparison) {

			assertEquals(x.getKey(), y.getKey());
			
			Object[] keys = Iterables.toArray(x.getKeys(), Object.class);
			
			matchedKeys.add(keys);
					
			comparisons.add(comparison);
		}
		
		@Override
		public void xMissing(MatchableGroup y) {
			
			Object[] keys = Iterables.toArray(y.getKey().getKeys(), 
					Object.class);
			
			xsMissing.add(keys);
		}
		
		@Override
		public void yMissing(MatchableGroup x) {
			
			Object[] keys = Iterables.toArray(x.getKey().getKeys(), 
					Object.class);
			
			ysMissing.add(keys);
		}
	}
	
	public void testNoKeysMatch() {

		MatchDefinition definition = new SimpleMatchDefinition(
				new String[] { "type" },
				new String[] { "quantity" },
				null);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);
		
		List<Fruit> fruitX = Arrays.asList(
				new Fruit("orange", 2),
				new Fruit("apple", 2));
		
		UnsortedBeanMatchables<Object> xs = 
			new UnsortedBeanMatchables<Object>(fruitX, factory);
		
		List<Fruit> fruitY = Arrays.asList(
				new Fruit("banana", 3),
				new Fruit("kiwi", 3));
		
		UnsortedBeanMatchables<Object> ys = 
			new UnsortedBeanMatchables<Object>(fruitY, factory);

		Results results = new Results();
		
		OrderedMatchablesComparer test = new OrderedMatchablesComparer(
				accessor,
				new ComparersByPropertyOrType(
						null, new DefaultComparersByType()),
				results);
		
		test.compare(xs, ys);
		
		assertEquals(2, results.xsMissing.size());
		
		assertEquals("banana", 
				results.xsMissing.get(0)[0]);
		assertEquals("kiwi", 
				results.xsMissing.get(1)[0]);
		
		assertEquals(2, results.ysMissing.size());
		
		assertEquals("apple", 
				results.ysMissing.get(0)[0]);
		assertEquals("orange",
				results.ysMissing.get(1)[0]);
		
		assertEquals(0, results.matchedKeys.size());
	}	
	
	public void testKeysMatchOneValueDoesnt() {
		
		MatchDefinition definition = new SimpleMatchDefinition(
				new String[] { "type" },
				new String[] { "quantity" },
				null
				);
				
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);
		
		List<Fruit> fruitX = Arrays.asList(
				new Fruit("orange", 3),
				new Fruit("pear", 4),
				new Fruit("apple", 6));
		
		UnsortedBeanMatchables<Object> xs = 
			new UnsortedBeanMatchables<Object>(fruitX, factory);
				
		List<Fruit> fruitY = Arrays.asList(
				new Fruit("pear", 4),
				new Fruit("apple", 5),
				new Fruit("orange", 3));
				
		UnsortedBeanMatchables<Object> ys = 
			new UnsortedBeanMatchables<Object>(fruitY, factory);

		Results results = new Results();
		
		OrderedMatchablesComparer test = new OrderedMatchablesComparer(
				accessor, 
				new ComparersByPropertyOrType(
						null, new DefaultComparersByType()),
				results);
		
		test.compare(xs, ys);

		assertEquals(0, results.xsMissing.size());
		assertEquals(0, results.ysMissing.size());
				
		assertEquals("apple", results.matchedKeys.get(0)[0]);
		assertEquals("orange", results.matchedKeys.get(1)[0]);
		assertEquals("pear", results.matchedKeys.get(2)[0]);
		
		assertEquals(false, results.comparisons.get(0).isEqual());
		assertEquals(true, results.comparisons.get(1).isEqual());
		assertEquals(true, results.comparisons.get(2).isEqual());		
	}

	public void testTwoXMissingOneYDuplicated() {
		
		MatchDefinition definition = new SimpleMatchDefinition(
				new String[] { "type" },
				new String[] { "quantity" },
				null);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);		

		List<Fruit> fruitX = Arrays.asList(		
				new Fruit("apple", 4),
				new Fruit("banana", 5));
				
		UnsortedBeanMatchables<Object> xs = 
			new UnsortedBeanMatchables<Object>(fruitX, factory);
		
		
		List<Fruit> fruitY = Arrays.asList(		
				new Fruit("apple", 4),
				new Fruit("apple", 5),
				new Fruit("banana", 5),
				new Fruit("orange", 2));
				
		UnsortedBeanMatchables<Object> ys = 
			new UnsortedBeanMatchables<Object>(fruitY, factory);
		
		Results results = new Results();
		
		OrderedMatchablesComparer test = new OrderedMatchablesComparer(
				accessor, 
				new ComparersByPropertyOrType(
						null, new DefaultComparersByType()),
				results);
		
		test.compare(xs, ys);
		
		assertEquals(2, results.matchedKeys.size());
		
		assertEquals("apple", results.matchedKeys.get(0)[0]);
		assertEquals("banana", results.matchedKeys.get(1)[0]);
		
		assertEquals(true, results.comparisons.get(0).isEqual());
		assertEquals(true, results.comparisons.get(1).isEqual());
		
		assertEquals(2, results.xsMissing.size());
		
		assertEquals("apple", results.xsMissing.get(0)[0]);
		assertEquals("orange", results.xsMissing.get(1)[0]);
		
		assertEquals(0, results.ysMissing.size());
	}
}
