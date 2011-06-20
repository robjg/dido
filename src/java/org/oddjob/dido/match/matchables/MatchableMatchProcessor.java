package org.oddjob.dido.match.matchables;


/**
 * Receives notifications of matches of {@link Matchable}s.
 * 
 * @see OrderedMatchablesComparer.
 * 
 * @author Rob
 *
 */
public interface MatchableMatchProcessor {
	
	/**
	 * Data is missing from X.
	 * 
	 * @param ys The y data.
	 */
	public void xMissing(MatchableGroup ys);
	
	/**
	 * Data is missing from Y.
	 * 
	 * @param xs The x data.
	 */
	public void yMissing(MatchableGroup xs);

	/**
	 * Two {@link Matchable}s have been matched
	 * by their keys.
	 * 
	 * @param x
	 * @param y
	 * @param comparison
	 */
	public void matched(Matchable x, Matchable y, 
			MatchableComparison comparison);

}
