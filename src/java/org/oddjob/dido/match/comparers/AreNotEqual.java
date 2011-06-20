package org.oddjob.dido.match.comparers;

import org.oddjob.dido.match.Comparison;

/**
 * A simple comparison where the result of a compare was that
 * two values are not equal.
 * 
 * @author rob
 *
 */
public class AreNotEqual implements Comparison {

	private final Object x;
	private final Object y;
	
	public AreNotEqual(Object x, Object y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean isEqual() {
		return false;
	}
	
	@Override
	public String getSummaryText() {
		return "" + x + "<>" + y;
	}
	
}
