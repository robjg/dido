package org.oddjob.dido.match.beans;

import org.oddjob.dido.match.Comparer;

/**
 * Provides {@link Comparer}s.
 * 
 * @author Rob
 *
 */
public interface BeanComparerProvider {

	/**
	 * Provide a {@link Comparer} for the given property
	 * and type.
	 * 
	 * @param property The name of the property.
	 * @param type The type of the property.
	 * 
	 * @return Never null.
	 */
	public <T> Comparer<T> comparerFor(String property, Class<T> type);
}
