package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.match.Comparison;

/**
 * Simple implementation of a {@link MatchableComparison}.
 * 
 * @author rob
 *
 */
public class SimpleMatchableComparision implements MatchableComparison {
	
	private final List<Comparison> comparisons = 
		new ArrayList<Comparison>();
	
	
	private int equal;
			
	public SimpleMatchableComparision(Iterable<? extends Comparison> comparisons) {
			
		for (Comparison comparison : comparisons) {
			this.comparisons.add(comparison);
			if (comparison.isEqual()) {
				equal++;
			}
		}
	}
	
	@Override
	public boolean isEqual() {
		return equal == comparisons.size();
	}
	
	@Override
	public Iterable<Comparison> getValueComparisons() {
		return comparisons;
	}
	
	@Override
	public String getSummaryText() {
		return "" + equal + "/" + comparisons.size() + " equal";
	}
}
