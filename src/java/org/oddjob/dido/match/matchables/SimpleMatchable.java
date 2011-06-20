package org.oddjob.dido.match.matchables;

import java.util.List;

/**
 * A simple implementation of a {@link Matchable}.
 * 
 * @author rob
 */
public class SimpleMatchable implements Matchable {

	private final MatchKey key;
	
	private final List<?> keys;
		
	private final List<?> values;
	
	private final List<?> others;
	
	private MatchableMetaData metaData;
	
	/**
	 * Only Constructor.
	 * 
	 * @param keys The key values.
	 * @param values The values to compare.
	 * @param others Other values.
	 */
	public SimpleMatchable(List<? extends Comparable<?>> keys,		
			List<?> values, List<?> others) {
		
		this.keys = keys;
		this.values = values;
		this.others = others;
		
		this.key = new SimpleMatchKey(keys);
	}
	
	@Override
	public MatchKey getKey() {
		return key;
	}

	@Override
	public Iterable<?> getKeys() {
		return keys;
	}
	
	@Override
	public Iterable<?> getValues() {
		return values;
	}
	
	@Override
	public Iterable<?> getOthers() {
		return others;
	}

	public MatchableMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MatchableMetaData metaData) {
		this.metaData = metaData;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ", key=" + keys;
	}
}