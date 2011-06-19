package org.oddjob.dido.match.comparers;

import org.oddjob.dido.match.Comparer;

/**
 * A {@link Comparer} that uses other {@link Comparer}s to create
 * it's comparison. 
 * 
 * @author rob
 *
 * @param <T>
 */
public interface HierarchicalComparer<T> extends Comparer<T> {

	/**
	 * Set the comparers the comparer can use.
	 * 
	 * @param comparersByType The comparers.
	 */
	public void setComparersByType(ComparersByType comparersByType);
}
