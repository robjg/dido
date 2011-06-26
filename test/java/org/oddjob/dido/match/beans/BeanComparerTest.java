package org.oddjob.dido.match.beans;

import junit.framework.TestCase;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.dido.match.Comparison;
import org.oddjob.dido.match.Iterables;
import org.oddjob.dido.match.matchables.MatchableComparison;

public class BeanComparerTest extends TestCase {

	public static class Fruit {
		
		private String type;
		
		private int quantity;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		
	}
	
	public void testCompare() {
		
		BeanComparer comparer = new BeanComparer(
				new String[] { "type", "quantity" },
				new BeanUtilsPropertyAccessor(),
				new ComparersByPropertyOrType());
		
		Fruit beanX = new Fruit();
		beanX.setType("apple");
		beanX.setQuantity(2);
		
		Fruit beanY = new Fruit();
		beanY.setType("apple");
		beanY.setQuantity(3);
		
		MatchableComparison comparison = comparer.compare(beanX, beanY);
		
		assertEquals(false, comparison.isEqual());
		
		Comparison[] comparisons = Iterables.toArray(
				comparison.getValueComparisons(), Comparison.class);
		
		assertEquals(2, comparisons.length);
		
		assertEquals(true, comparisons[0].isEqual());
		assertEquals(false, comparisons[1].isEqual());
	}
}
