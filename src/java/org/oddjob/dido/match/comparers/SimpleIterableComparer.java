package org.oddjob.dido.match.comparers;

import java.util.Iterator;

import org.oddjob.dido.match.Comparer;
import org.oddjob.dido.match.Comparison;

/**
 * Compares to {@code Iterable}s.
 * 
 * @author rob
 *
 */
public class SimpleIterableComparer 
implements HierarchicalComparer<Iterable<?>>{

	private ComparersByType comparers;
	
	@Override
	public void setComparersByType(ComparersByType comparersByType) {
		this.comparers = comparersByType;
	}
	
	@Override
	public Comparison compare(Iterable<?> x, Iterable<?> y) {
		if (x == null || y == null) {
			return null;
		}
		
		Iterator<?> iterX = x.iterator();
		Iterator<?> iterY = y.iterator();
		
		int same = 0;
		int different = 0;
		int xsMissing = 0;
		int ysMissing = 0;

		while (iterX.hasNext() && iterY.hasNext()) {
			Object eX = iterX.next();
			Object eY = iterY.next();
			
			if (eX == null && eY == null) {
				++same;
			}
			if (eX != null || eY != null) {
				++different;
			}

			@SuppressWarnings("unchecked")
			Comparer<Object> comparer = (Comparer<Object>) 
				comparers.comparerFor(eX.getClass());
			
			Comparison eComparison = 
				comparer.compare(eX, eY);
			if (eComparison.isEqual()) {
				++same;
			}
			else {
				++different;
			}
		}
		
		while (iterX.hasNext()) {
			iterX.next();
			++ysMissing;
		}
		
		while (iterY.hasNext()) {
			iterY.next();
			++xsMissing;
		}
		
		return new MultiItemComparison(
				xsMissing, ysMissing, different, same);
	}
	
	@Override
	public Class<?> getType() {
		return Iterable.class;
	}	
}
