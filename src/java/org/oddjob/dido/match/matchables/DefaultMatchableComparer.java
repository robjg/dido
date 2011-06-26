package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.match.Comparer;
import org.oddjob.dido.match.Comparison;
import org.oddjob.dido.match.beans.BeanComparerProvider;

/**
 * Compares two {@link Matchable}s.
 * 
 * @author Rob
 *
 */
public class DefaultMatchableComparer implements MatchableComparer {
	
	private final BeanComparerProvider comparerProvider;
			
	public DefaultMatchableComparer(
			BeanComparerProvider comparerProvider) {
		this.comparerProvider = comparerProvider;
	}
	
	/**
	 * Compare two {@link Matchable}s.
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return A {@link Comparison}. Never null.
	 */
	@Override
	public MatchableComparison compare(Matchable x, Matchable y) {
				
		if (x == null || y == null) {
			throw new NullPointerException("Matchables must not be null.");
		}

		List<Comparison> comparisons = new ArrayList<Comparison>();		

		MatchableIterable<?> values = 
			new MatchableIterable<Object>(
				x.getMetaData().getValueProperties(), 
				x.getValues(), y.getValues());
	
		for (MatchableIterable.MatchableSet<?> set : values) {

			Object valueX = set.getValueX();
			Object valueY = set.getValueY();
			
			String propertyName = set.getPropertyName();
			Class<?> propertyType = 
				x.getMetaData().getPropertyType(propertyName);
			
			Comparison comparison = 
				inferComparerType(propertyName, valueX, valueY, 
						propertyType);
			
			comparisons.add(comparison);			
		}		
		
		return new SimpleMatchableComparision(comparisons);
	}

	private <T> Comparison inferComparerType(String propertyName,
			Object rawX, Object rawY, Class<T> type) {
		
		Comparer<T> differentiator = 
			comparerProvider.comparerFor(propertyName, 
					type);
	
		return differentiator.compare(
				type.cast(rawX), 
				type.cast(rawY));
	}
	
	public BeanComparerProvider getComparerProvider() {
		return comparerProvider;
	}
	
	@Override
	public Class<Matchable> getType() {
		return Matchable.class;
	}
}
