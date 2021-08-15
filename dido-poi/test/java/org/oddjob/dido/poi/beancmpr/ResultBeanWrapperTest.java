package org.oddjob.dido.poi.beancmpr;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;

import junit.framework.TestCase;

public class ResultBeanWrapperTest extends TestCase {

	public void testDifferent() {
		
		FakeResultsBean.Builder builder = 
				new FakeResultsBean.Builder(3);
		
		builder.addKey("id", new Integer(10));
		builder.addKey("region", "UK");

		builder.addComparison("quantity", new Integer(15), 
				new Integer(16), -1);
		
		builder.addComparison("price", new Double(22.4), 
				new Float(22.4), 0);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		ResultBeanWrapper test = new ResultBeanWrapper(
				accessor, builder.build()); 
		
		assertEquals(3, test.getResultType());
		
		assertEquals(new Integer(10), test.getKeys().get("id"));
		assertEquals("region", test.getKeys().keySet().toArray()[1]);
		
		ComparisonBeanWrapper comparison = test.getComparisons().get("quantity");
		assertEquals(new Integer(15), comparison.getX());
		assertEquals(new Integer(16), comparison.getY());
		assertEquals(Object.class, comparison.getTypeOfX());
		assertEquals(Object.class, comparison.getTypeOfY());
		assertEquals(-1, comparison.getResult());
	}
}
