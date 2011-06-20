package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An adapter that converts an {@code Iterable} of bean to 
 * an {@code Iterable} of {@link MatchableGroup}s suitable for use by
 * an {@link OrderedMatchablesComparer}.
 * <p>
 * The beans must be in order as given by their key property
 * values.
 * 
 * @author rob
 *
 */
public class SortedBeanMatchables<T> implements Iterable<MatchableGroup> {
	
	private final Iterable<? extends T> iterable;
	
	private final MatchableFactory<T> factory;
	
	public SortedBeanMatchables(Iterable<? extends T> iterable, 
			MatchableFactory<T> factory) {
		this.iterable = iterable;
		this.factory = factory;
	}
		
	@Override
	public Iterator<MatchableGroup> iterator() {
		
		return new Iterator<MatchableGroup>() {

			private Matchable last;

			private Iterator<? extends T> iterator = iterable.iterator();
			
			@Override
			public boolean hasNext() {
				return last != null || iterator.hasNext();
			}
			
			@Override
			public MatchableGroup next() {
				
				final List<Matchable> group = new ArrayList<Matchable>();
				
				if (last != null) {
					group.add(last);
					last = null;
				}
				
				while (iterator.hasNext()) {
					
					Matchable next = factory.createMatchable(iterator.next());

					if (group.size() > 0 && 
							!group.get(0).getKey().equals(next.getKey())) {
						last = next;
						break;	
					}
					else {
						group.add(next);
					}
				}

				if (group.size() == 0) {
					throw new NoSuchElementException();
				}
				
				return new MatchableGroup() {
					
					@Override
					public int getSize() {
						return group.size();
					}
					
					@Override
					public MatchKey getKey() {
						return group.get(0).getKey();
					}
					
					@Override
					public Iterable<Matchable> getGroup() {
						return group;
					}
					
					@Override
					public String toString() {
						return MatchableGroup.class.getSimpleName() + 
							", " + getKey();
					}
				};
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}	
}
