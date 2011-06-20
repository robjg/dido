package org.oddjob.dido.match.matchables;

import org.oddjob.dido.match.Comparer;


/**
 * Something that can compare two {@link Matchable}s.
 * 
 * @author Rob
 *
 */
public interface MatchableComparer extends Comparer<Matchable> {

	public MatchableComparison compare(Matchable x, Matchable y);	
}
