package org.oddjob.dido.match.comparers;

import org.oddjob.dido.match.Comparison;

/**
 * A simple comparison where the result of a compare was that
 * two values are equal.
 * 
 * @author rob
 *
 */
public class AreEqual implements Comparison {

	
	@Override
	public boolean isEqual() {
		return true;
	}
	
	@Override
	public String getSummaryText() {
		return "";
	}
}
