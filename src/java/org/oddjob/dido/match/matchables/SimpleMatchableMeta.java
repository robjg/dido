package org.oddjob.dido.match.matchables;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.match.MatchDefinition;

/**
 * A simple implementation of {@link MatchableMetaData}.
 * 
 * @author rob
 *
 */
public class SimpleMatchableMeta implements MatchableMetaData {

	private static final Map<Class<?>, Class<?>> PRIMATIVES = 
		new HashMap<Class<?>, Class<?>>();
	
	static {
		PRIMATIVES.put(boolean.class, Boolean.class);
		PRIMATIVES.put(byte.class, Byte.class);
		PRIMATIVES.put(short.class, Short.class);
		PRIMATIVES.put(char.class, Character.class);
		PRIMATIVES.put(int.class, Integer.class);
		PRIMATIVES.put(long.class, Long.class);
		PRIMATIVES.put(float.class, Float.class);
		PRIMATIVES.put(double.class, Double.class);		
	}
	
	private final MatchDefinition definition;
	
	private final Map<String, Class<?>> types;
	
	public SimpleMatchableMeta(MatchDefinition definition,
			Map<String, Class<?>> types) {
		this.definition = definition;
		this.types = new HashMap<String, Class<?>>();
		for (Map.Entry<String, Class<?>> entry: types.entrySet()) {
			Class<?> type = entry.getValue();
			if (type.isPrimitive()) {
				type = PRIMATIVES.get(type);
			}
			this.types.put(entry.getKey(), type);
		}
	}
	
	@Override
	public Iterable<String> getKeyProperties() {
		return definition.getKeyProperties();
	}
	
	@Override
	public Iterable<String> getValueProperties() {
		return definition.getValueProperties();
	}
	
	@Override
	public Iterable<String> getOtherProperties() {
		return definition.getOtherProperties();
	}
	
	@Override
	public Class<?> getPropertyType(String name) {
		return types.get(name);
	}
}
