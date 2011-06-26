package org.oddjob.dido.match.matchables;

import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.dido.match.Comparison;
import org.oddjob.dido.match.Iterables;
import org.oddjob.dido.match.beans.ComparersByPropertyOrType;

public class DefaultMatchableComparerTest extends TestCase {

	private class MyMatchable extends MockMatchable {
	
		final Iterable<?> values;
		
		public MyMatchable(Object... values) {
			this.values = Arrays.asList(values);
		}
		
		@Override
		public MatchableMetaData getMetaData() {
			return new MockMatchableMetaData() {
				@Override
				public Iterable<String> getValueProperties() {
					return Arrays.asList("fruit", "colour");
				}
				
				@Override
				public Class<?> getPropertyType(String name) {
					return String.class;
				}
			};
		}
		
		@Override
		public Iterable<?> getValues() {
			return values;
		}
	}
	
	
	public void testCompareEqual() {
		
		DefaultMatchableComparer test = new DefaultMatchableComparer(
				new ComparersByPropertyOrType(
						null, null));

		MyMatchable x = new MyMatchable("apple", "red");
		MyMatchable y = new MyMatchable("apple", "red");
		
		MatchableComparison result = test.compare(x, y);
		
		assertEquals(true, result.isEqual());
		
		Comparison[] comparisons = Iterables.toArray(
				result.getValueComparisons(), Comparison.class);
		
		assertEquals(2, comparisons.length);
		
		assertEquals(true, comparisons[0].isEqual());
		assertEquals(true, comparisons[1].isEqual());
	}
	
	public void testCompareNotEqual() {
		
		DefaultMatchableComparer test = new DefaultMatchableComparer(
				new ComparersByPropertyOrType(
						null, null));

		MyMatchable x = new MyMatchable("apple", "red");
		MyMatchable y = new MyMatchable("apple", "green");
		
		MatchableComparison result = test.compare(x, y);
		
		assertEquals(false, result.isEqual());
		
		Comparison[] comparisons = Iterables.toArray(result.getValueComparisons(), 
				Comparison.class);
		
		assertEquals(2, comparisons.length);
		
		assertEquals(true, comparisons[0].isEqual());
		assertEquals(false, comparisons[1].isEqual());
	}
}
