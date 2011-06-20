package org.oddjob.dido.match.matchables;

import org.oddjob.dido.match.Comparison;

/**
 * The {@link Comparison}s between the values of two {@link Matchable}s.
 * 
 * @author Rob
 *
 */
public interface MatchableComparison extends Comparison {

	/**
	 * Provides an {@code Iterable} of the individual {@link Comparison}s
	 * between the values of two {@link Matchable}s.
	 * 
	 * @return
	 */
	public Iterable<Comparison> getValueComparisons();

}
