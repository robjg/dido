package org.oddjob.dido.match.matchables;

/**
 * The key for a {@link Matchable}.
 * 
 * @author rob
 *
 */
public interface MatchKey extends Comparable<MatchKey> {

	/**
	 * Get the key elements.
	 * 
	 * @return An {@code Iterable} of {@code Comparable}s. Never null.
	 */
	public Iterable<? extends Comparable<?>> getKeys();
}