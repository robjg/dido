package org.oddjob.dido.match.comparers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	public MultiItemComparison compare(Iterable<?> x, Iterable<?> y) {
		if (x == null || y == null) {
			return null;
		}
				
		List<Object> yCopy = new ArrayList<Object>();
		for (Object o : y) {
			yCopy.add(o);
		}
		
		int same = 0;
		int different = 0;
		int xsMissing = 0;
		int ysMissing = 0;

		int xCount = 0;
		int yCount = yCopy.size();
		
		for (Object eX : x) {
			
			if (!yCopy.isEmpty()) {
				
				boolean found = false;
				
				for (Iterator<Object> itY = yCopy.iterator(); itY.hasNext(); ) {
					
					Object eY = itY.next();
					
					@SuppressWarnings("unchecked")
					Comparer<Object> comparer = (Comparer<Object>) 
						comparers.comparerFor(eX.getClass());
					
					Comparison eComparison = 
						comparer.compare(eX, eY);
					
					if (eComparison.isEqual()) {
						itY.remove();
						found = true;
						break;
					}
				}
				
				if (found) {
					++same;
				}
				else {
					++different;
				}			
			}
			
			++xCount;
		}
		
		if (yCount > xCount) {
			xsMissing = yCount - xCount;
		}
		
		if (xCount > yCount) {
			ysMissing = xCount - yCount;
			different -= ysMissing;
		}
		
		return new MultiItemComparison(
				xsMissing, ysMissing, different, same);
	}
	
	@Override
	public Class<?> getType() {
		return Iterable.class;
	}	
}
