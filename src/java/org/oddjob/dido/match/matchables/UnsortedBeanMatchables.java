package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Takes an unsorted {@code Iterable} of beans and provides an 
 * {code Iterable} of {@link OrderedMatchables}s suitable for use by
 * an {@link OrderedMatchablesComparer}.
 * 
 * @param <T>
 * @author Rob
 *
 */
public class UnsortedBeanMatchables<T> 
implements Iterable<MatchableGroup> {
	
	/** Collect groups of matchables sorted by key. */
	private final SortedMap<MatchKey, List<Matchable>> data = 
		new TreeMap<MatchKey, List<Matchable>>();
	
	/**
	 * Constructor.
	 * 
	 * @param iterable The beans
	 * @param factory The factory to create the {link Matchable}s.
	 */
	public UnsortedBeanMatchables(Iterable<? extends T> iterable, 
			MatchableFactory<T> factory) {
		
		for (T thing : iterable) {
			Matchable matchable = factory.createMatchable(thing);			
			List<Matchable> group = data.get(matchable.getKey());
			
			if (group == null) {
				group = new ArrayList<Matchable>();
				this.data.put(matchable.getKey(), group);
			}
			group.add(matchable);
		}		
	}
	
	@Override
	public Iterator<MatchableGroup> iterator() {
		return new Iterator<MatchableGroup>() {
			
			private Iterator<Map.Entry<MatchKey, List<Matchable>>> iterator 
				= data.entrySet().iterator();
			
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			
			@Override
			public MatchableGroup next() {
				
				final Map.Entry<MatchKey, List<Matchable>> next = 
					iterator.next();
				
				return new MatchableGroup() {
					
					@Override
					public MatchKey getKey() {
						return next.getKey();
					}
					
					@Override
					public Iterable<Matchable> getGroup() {
						return next.getValue();
					}
					
					@Override
					public int getSize() {
						return next.getValue().size();
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
