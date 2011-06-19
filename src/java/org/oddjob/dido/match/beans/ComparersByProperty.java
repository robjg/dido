package org.oddjob.dido.match.beans;

import org.oddjob.dido.match.Comparer;

/**
 * Provide a {@link Comparer} for a property. 
 * 
 * @author rob
 *
 */
public interface ComparersByProperty {

	/**
	 * Provide a {@link Comparer}.
	 * 
	 * @param propertyName The property.
	 * @return The comparer. May be null.
	 */
	public Comparer<?> getComparerForProperty(String propertyName);
}
