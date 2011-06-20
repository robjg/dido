package org.oddjob.dido.match.matchables;

import org.oddjob.dido.match.comparers.MultiItemComparison;

/**
 * A {@link MatchableMatchProcessor} that counts matches, before passing the result
 * onto a delegate.
 * 
 * @author rob
 *
 */
public class ComparisonGatheringProcessor 
implements MatchableMatchProcessor {

	private final MatchableMatchProcessor delegate;
	
	private int xsMissing;
	
	private int ysMissing;

	private int different;
	
	private int same;
	
	public ComparisonGatheringProcessor(MatchableMatchProcessor delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void xMissing(MatchableGroup ys) {
		xsMissing += ys.getSize();
		if (delegate != null) {
			delegate.xMissing(ys);
		}
	}
	
	@Override
	public void yMissing(MatchableGroup xs) {
		ysMissing += xs.getSize();
		if (delegate != null) {
			delegate.yMissing(xs);
		}
	}
	
	@Override
	public void matched(Matchable x, Matchable y,
			MatchableComparison comparison) {
		if (comparison.isEqual()) {
			++same;
		}
		else {
			++different;
		}
		if (delegate != null) {
			delegate.matched(x, y, comparison);
		}
	}
	
	public MultiItemComparison getComparison() {
		return new MultiItemComparison(xsMissing, ysMissing, different, same);
	}
	
}
