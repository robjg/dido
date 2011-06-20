package org.oddjob.dido.match.comparers;

import org.oddjob.dido.match.Comparer;

/**
 * A {@link Comparer that uses a number of child compares to find
 * the correct comparer. The comparers are queried in the order
 * they are specified.
 * 
 * @author rob
 *
 */
public class CompositeComparersByType implements ComparersByType {

	private ComparersByType[] comparers;
	
	public CompositeComparersByType(ComparersByType... comparers) {
		this.comparers = comparers;
	}
	
	@Override
	public <T> Comparer<T> comparerFor(Class<T> type) {
		for (ComparersByType each : comparers) {
			Comparer<T> result = each.comparerFor(type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	@Override
	public void injectComparers(ComparersByType comparers) {
		for (ComparersByType each : this.comparers) {
			each.injectComparers(comparers);
		}
	}
}
