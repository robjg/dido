package org.oddjob.dido.match.comparers;

import java.util.Arrays;

import org.oddjob.dido.match.Comparison;

/**
 * Compares to Arrays of Objects.
 * 
 * @author rob
 *
 */
public class ArrayComparer 
implements HierarchicalComparer<Object[]>{

	private final SimpleIterableComparer iterableComparer
		= new SimpleIterableComparer();
	
	@Override
	public void setComparersByType(ComparersByType comparersByType) {
		iterableComparer.setComparersByType(comparersByType);
	}
	
	@Override
	public Comparison compare(Object[] x, Object[] y) {
		if (x == null || y == null) {
			return null;
		}
		return iterableComparer.compare(Arrays.asList(x), Arrays.asList(y));
	}
	
	@Override
	public Class<?> getType() {
		return Object[].class;
	}	
}
