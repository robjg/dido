package org.oddjob.dido.match.matchables;

import java.util.Iterator;

import org.oddjob.dido.match.matchables.MatchableIterable.MatchableSet;

/**
 * Provide an {@code Iterable} over two sets of values and their names.
 * 
 * @author rob
 *
 * @param <T>
 */
public class MatchableIterable<T> implements Iterable<MatchableSet<T>> {

	private final Iterable<String> propertyNames;
	private final Iterable<? extends T> valuesX;
	private final Iterable<? extends T> valuesY;
	
	public MatchableIterable(Iterable<String> propertyNames,
			Iterable<? extends T> valuesX, 
			Iterable<? extends T> valuesY) {
		
		this.propertyNames = propertyNames;
		this.valuesX = valuesX;
		this.valuesY = valuesY;
	}
	
	@Override
	public Iterator<MatchableSet<T>> iterator() {
		return new Iterator<MatchableSet<T>>() {
			
			Iterator<String> namesIterator = propertyNames.iterator();
			Iterator<? extends T> xIterator = valuesX.iterator();
			Iterator<? extends T> yIterator = valuesY.iterator();
			
			@Override
			public boolean hasNext() {
				return namesIterator.hasNext();
			}
			@Override
			public MatchableSet<T> next() {
				final String name = namesIterator.next();
				final T x = xIterator.next();
				final T y = yIterator.next();
				return new MatchableSet<T>() {
					
					@Override
					public T getValueY() {
						return y;
					}
					
					@Override
					public T getValueX() {
						return x;
					}
					
					@Override
					public String getPropertyName() {
						return name;
					}
				};
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
		
	public interface MatchableSet<T> {
		
		public String getPropertyName();
		
		public T getValueX();
		
		public T getValueY();
	}
	
}
