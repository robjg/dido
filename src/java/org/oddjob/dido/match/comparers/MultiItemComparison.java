package org.oddjob.dido.match.comparers;

import org.oddjob.dido.match.Comparison;


/**
 * A {@link Comparison} that is the result of comparing many things.
 * 
 * @author rob
 *
 */
public class MultiItemComparison implements Comparison {

	private final int xsMissing;
	
	private final int ysMissing;

	private final int different;
	
	private final int same;
	
	
	public MultiItemComparison(
			int xsMissing,
			int ysMissing,
			int different,
			int same) {
		
		this.xsMissing = xsMissing;
		this.ysMissing = ysMissing;
		this.different = different;
		this.same = same;
	}

	@Override
	public boolean isEqual() {
		return different == 0 && xsMissing == 0 && ysMissing == 0;
	}
	
	@Override
	public String getSummaryText() {
		if (isEqual()) {
			return "Equal, " + same + " matched";
		}
		else {
			return "" + (xsMissing + ysMissing + different) +
			  " differences";
		}
	}
	
	public int getXsMissing() {
		return xsMissing;
	}
	
	public int getYsMissing() {
		return ysMissing;
	}
	
	public int getDifferent() {
		return different;
	}
	
	public int getSame() {
		return same;
	}
}
