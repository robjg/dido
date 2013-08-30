package org.oddjob.dido.poi.utils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Create a type based on the class. Used to create Cells of the write
 * type.
 * 
 * @author rob
 *
 * @param <T> The type of thing created by the factory.
 */
abstract public class CellTypeFactory<T> {

	private final static Set<Class<?>> NUMERIC_PRIMATIVES = 
			new HashSet<Class<?>>();
		
	static {
		
		NUMERIC_PRIMATIVES.add(byte.class);
		NUMERIC_PRIMATIVES.add(short.class);
		NUMERIC_PRIMATIVES.add(int.class);
		NUMERIC_PRIMATIVES.add(long.class);
		NUMERIC_PRIMATIVES.add(float.class);
		NUMERIC_PRIMATIVES.add(double.class);
	}
	
	public T createFor(Class<?> propertyType) {
		
		if (NUMERIC_PRIMATIVES.contains(propertyType) || 
				Number.class.isAssignableFrom(propertyType)) {
			return createNumeric();
		}
		else if (Boolean.class == propertyType || 
				boolean.class == propertyType) {
			return createBoolean();
		}
		else if (Date.class.isAssignableFrom(propertyType)) {
			return createDate();
		}
		else {
			return createText();
		}
	}

	abstract protected T createNumeric();
	
	abstract protected T createBoolean();
	
	abstract protected T createText();
	
	abstract protected T createDate();
}
