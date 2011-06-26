package org.oddjob.dido.match.beans;

import org.oddjob.dido.match.Comparer;
import org.oddjob.dido.match.comparers.ComparersByType;
import org.oddjob.dido.match.comparers.CompositeComparersByType;
import org.oddjob.dido.match.comparers.DefaultComparersByType;

/**
 * Collects together {@link ComparersByProperty} and {@link ComparersByType}
 * <p>
 * Also provides a base set of {@link DefaultComparersByType}.
 * 
 * @author rob
 *
 */
public class ComparersByPropertyOrType implements BeanComparerProvider {
	
	private final ComparersByProperty comparersByProperty;
	
	private final ComparersByType comparersByType;
	
	public ComparersByPropertyOrType(
			ComparersByProperty comparersByProperty,
			ComparersByType comparersByType) {
		if (comparersByType == null) {
			this.comparersByType = new DefaultComparersByType(); 
		}
		else {
			this.comparersByType = new CompositeComparersByType(
					comparersByType, new DefaultComparersByType());
		}
		
		this.comparersByType.injectComparers(this.comparersByType);
		
		this.comparersByProperty = comparersByProperty;
	}
	
	public ComparersByPropertyOrType() {
		this(null, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> Comparer<T> comparerFor(String property, Class<T> type) {

		Comparer<T> comparer = null;
		if (comparersByProperty != null) {
			comparer = 
				(Comparer<T>) comparersByProperty.getComparerForProperty(
						property);
		}
		
		if (comparer != null) {
			return comparer;
		}
		
		return comparersByType.comparerFor(type);
	}
}
