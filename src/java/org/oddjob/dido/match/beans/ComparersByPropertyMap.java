package org.oddjob.dido.match.beans;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.match.Comparer;

/**
 * Collects {@link Comparer}s by property name.
 * 
 * @author rob
 *
 */
public class ComparersByPropertyMap implements ComparersByProperty {

	public final Map<String, Comparer<?>> comparersByProperty = 
		new HashMap<String, Comparer<?>>();


	public void setComparerForProperty(String property, Comparer<?> comparer) {
		comparersByProperty.put(property, comparer);
	}
	
	@Override
	public Comparer<?> getComparerForProperty(String propertyName) {
		return comparersByProperty.get(propertyName);
	}
	
}
