package org.oddjob.dido.match.matchables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.match.MatchDefinition;

/**
 * A {@link MatchableFactory} that creates {@link Matchable}s from beans.
 * 
 * @author rob
 *
 */
public class BeanMatchableFactory implements MatchableFactory<Object> {

	private final MatchDefinition definition;
	
	private final PropertyAccessor accessor;
	
	private MatchableMetaData metaData;
	
	public BeanMatchableFactory(MatchDefinition definition,
			PropertyAccessor accessor) {
		this.definition = definition;
		this.accessor = accessor;
	}
	
	@Override
	public Matchable createMatchable(Object bean) {

		if (bean == null) {
			return null;
		}
		
		if (metaData == null) {
			metaData = new SimpleMatchableMeta(definition, 
					typesFor(bean));
		}
		
		List<Comparable<?>> keys = strip(bean, definition.getKeyProperties());		
		List<?> comparables = strip(bean, definition.getValueProperties());
		List<?> others = strip(bean, definition.getOtherProperties());
		
		SimpleMatchable matchable = 
			new SimpleMatchable(keys, comparables, others);
		
		matchable.setMetaData(metaData);
		
		return matchable;
	}
	
	@SuppressWarnings("unchecked")
	<T> List<T> strip(Object bean, Iterable<String> names) {
		
		List<T> values = new ArrayList<T>();
		for (String name : names) {
			values.add( (T)accessor.getProperty(bean, name));
		}
		return values;
	}
	
	private Map<String, Class<?>> typesFor(Object bean) {
		
		Map<String, Class<?>> types = new HashMap<String, Class<?>>();
		
		BeanOverview overview = accessor.getBeanOverview(bean.getClass());

		for (String name : definition.getKeyProperties()) {
			types.put(name, overview.getPropertyType(name));
		}
		for (String name : definition.getValueProperties()) {
			types.put(name, overview.getPropertyType(name));
		}
		for (String name : definition.getOtherProperties()) {
			types.put(name, overview.getPropertyType(name));
		}
		
		return types;
	}	
}

