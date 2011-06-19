package org.oddjob.dido.match.comparers;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.match.Comparer;

/**
 * An implementation of {@link ComparersByType} backed by a {@code List}.
 * 
 * @author rob
 *
 */
public class ComparersByTypeList implements ComparersByType {

	private final List<Comparer<?>> comparers = new ArrayList<Comparer<?>>();

	/**
	 * Setter for {@link Comparer}s.
	 * 
	 * @param index
	 * @param comparer
	 */
	public void setComparer(int index, Comparer<?> comparer) {
		if (comparer == null) {
			comparers.remove(index);
		}
		else {
			comparers.add(index, comparer);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> Comparer<T> comparerFor(Class<T> type) {
		for (Comparer<?> comparer : comparers) {
			if (comparer.getType().isAssignableFrom(type)) {
				return (Comparer<T>) comparer;
			}
		}
		return null;
	}
	
	@Override
	public void injectComparers(ComparersByType comparers) {
		for (Comparer<?> comparer : this.comparers) {
			if (comparer instanceof HierarchicalComparer<?>) {
				((HierarchicalComparer<?>) comparer).setComparersByType(
						comparers);
			}
		}
	}
}
