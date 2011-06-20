package org.oddjob.dido.match;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.match.matchables.Matchable;

public class SplitMatchable {

	public Object[] keys;
	
	public Object[] values;
	
	public Object[] others;
	
	public static SplitMatchable split(Matchable matchable) {
		SplitMatchable split = new SplitMatchable();
		
		split.keys = Iterables.toArray(
				matchable.getKeys(), Object.class);
		
		split.values = Iterables.toArray(
				matchable.getValues(), Object.class);
		
		split.others = Iterables.toArray(
				matchable.getOthers(), Object.class);
				
		return split;
	}
	
	public static SplitMatchable[] split(Iterable<Matchable> matchables) {
		
		List<SplitMatchable> list = new ArrayList<SplitMatchable>();
		
		for (Matchable matchable : matchables) {
			
			SplitMatchable split = split(matchable);
			
			list.add(split);
		}
		
		return list.toArray(new SplitMatchable[list.size()]);		
	}
}
