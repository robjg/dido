package org.oddjob.dido.match.matchables;

import java.util.Iterator;

/**
 * A simple implementation of {@link MatchKey}.
 * 
 * @author rob
 *
 */
public class SimpleMatchKey implements MatchKey {

	private final Iterable<? extends Comparable<?>> keys;

	/**
	 * Constructor.
	 * 
	 * @param components The component values of the key.
	 */
	public SimpleMatchKey(Iterable<? extends Comparable<?>> components) {
		this.keys = components;
	}
	
	@Override
	public Iterable<? extends Comparable<?>> getKeys() {
		return keys;
	}
	
	public boolean equals(Object other) {
		if (other == this)
		    return true;
		
		if (!(other instanceof MatchKey))
		    return false;

		return compareTo((MatchKey) other) == 0;
	}

	public int hashCode() {
		int hashCode = 1;
		Iterator<?> i = keys.iterator();
		while (i.hasNext()) {
		    Object obj = i.next();
		    hashCode = 31 * hashCode + (obj==null ? 0 : obj.hashCode());
		}
		return hashCode;
	}

	public int compareTo(MatchKey other) throws ClassCastException {
		
		Iterator<? extends Comparable<?>> e1 = keys.iterator();
		Iterator<? extends Comparable<?>> e2 = other.getKeys().iterator();
		
		while(e1.hasNext() && e2.hasNext()) {
			
		    Comparable<?> o1 = e1.next();
		    Comparable<?> o2 = e2.next();
		    
		    if (o1 == null && o2 == null) {
		    	continue;
		    }
		    if (o1 == null) {
		    	return -1;
		    }
		    if (o2 == null) {
		    	return 1;
		    }
		    
			int result = objectCompare(o1,o2);
			if (result != 0) {
				return result;
			}
		}
		
		if (e1.hasNext() || e2.hasNext()) {
			throw new IllegalArgumentException(
					"Keys must be the same length.");
		}
		
		return 0;		
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int objectCompare(
			Comparable o1, Comparable o2) {
		return o1.compareTo(o2);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ", keys=" + keys;
	}
}
