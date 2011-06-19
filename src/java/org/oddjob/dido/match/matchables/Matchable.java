package org.oddjob.dido.match.matchables;

import org.oddjob.dido.match.MatchDefinition;

/**
 * Something that's matchable against another <code>Matchable</code> 
 * for the same {@link MatchDefinition}.
 * 
 * @author Rob
 *
 */
public interface Matchable {

	/**
	 * Get the key to match on.
	 * 
	 * @return The key.
	 */
	public MatchKey getKey();
	
	/**
	 * Get the keys.
	 * 
	 * @return The keys.
	 * 
	 */
	public Iterable<?> getKeys();
	
	/**
	 * Get the values for comparison.
	 * 
	 * @return The values to compare.
	 * 
	 */
	public Iterable<?> getValues();
	
	/**
	 * Get the others.
	 * 
	 * @return The others.
	 * 
	 */
	public Iterable<?> getOthers();
	
	/**
	 * Get the meta data that describe this matchable.
	 * 
	 * @return The meta data.
	 */
	public MatchableMetaData getMetaData();
}
