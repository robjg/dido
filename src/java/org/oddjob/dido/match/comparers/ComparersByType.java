package org.oddjob.dido.match.comparers;

import org.oddjob.dido.match.Comparer;

/**
 * A collection of {@link Comparer}s.
 * 
 * @author rob
 *
 */
public interface ComparersByType {

	/**
	 * Find a {@link Comparer} for a given type.
	 * 
	 * @param <T> The type of the type.
	 * @param type The type.
	 * 
	 * @return The comparer or null if one can't be found for the type.
	 */
	public <T> Comparer<T> comparerFor(Class<T> type);
	
	/**
	 * Used by owner of comparers to inject the master comparer into
	 * {@link HierarchicalComparer}s.
	 * 
	 * @param comparers The master comparers.
	 */
	public void injectComparers(ComparersByType comparers);
	
}
