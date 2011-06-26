package org.oddjob.dido.match.beans;

import org.oddjob.dido.match.matchables.Matchable;
import org.oddjob.dido.match.matchables.MatchableComparison;

/**
 * Creates a bean who's properties are the results.
 * 
 * @author rob
 *
 */
public interface MatchResultBeanFactory {

	
	public Object createResult(Matchable x, Matchable y, 
			MatchableComparison matchableComparison);	
	
}
