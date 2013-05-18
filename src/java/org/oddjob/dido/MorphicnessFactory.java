package org.oddjob.dido;

import java.util.Map;
import java.util.TreeMap;

import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.BeanView;
import org.oddjob.arooa.reflect.FallbackBeanView;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class MorphicnessFactory {

	private final PropertyAccessor accessor;
	
	public MorphicnessFactory(PropertyAccessor accessor) {
		this.accessor = accessor;
	}
		
	public Morphicness readMorphicnessFor(ArooaClass arooaClass) {
		return readMorphicnessFor(arooaClass, null);
	}		
		
	public Morphicness readMorphicnessFor(
			ArooaClass arooaClass, BeanView beanView ) {

		BeanOverview overview = 
				arooaClass.getBeanOverview(accessor);
						
		if (beanView == null) {
			beanView = new FallbackBeanView(accessor, arooaClass);
		}

		final Map<String, Class<?>> types = new TreeMap<String, Class<?>>();
		final Map<String, String> titles = new TreeMap<String, String>();
		
		for (String property : beanView.getProperties()) {
				
			if ("class".equals(property)) {
				continue;
			}

			if (overview.hasReadableProperty(property) &&
					!overview.isIndexed(property) &&
					!overview.isMapped(property)) {

				types.put(property, overview.getPropertyType(property));
				titles.put(property, beanView.titleFor(property));
			}
		}
		
		return new Morphicness() {
			
			@Override
			public String[] getNames() {
				return types.keySet().toArray(new String[types.size()]);
			}
			
			@Override
			public Class<?> typeOf(String name) {
				return types.get(name);
			}
			
			@Override
			public String titleFor(String name) {
				return titles.get(name);
			}
		};
			
	}
	
	public Morphicness writeMorphicnessFor(ArooaClass arooaClass) {
		return writeMorphicnessFor(arooaClass, null);
	}		
		
	public Morphicness writeMorphicnessFor(
			ArooaClass arooaClass, BeanView beanView ) {

		BeanOverview overview = 
				arooaClass.getBeanOverview(accessor);
						
		if (beanView == null) {
			beanView = new FallbackBeanView(accessor, arooaClass);
		}

		final Map<String, Class<?>> types = new TreeMap<String, Class<?>>();
		final Map<String, String> titles = new TreeMap<String, String>();
		
		for (String property : beanView.getProperties()) {
				
			if ("class".equals(property)) {
				continue;
			}

			if (overview.hasWriteableProperty(property) &&
					!overview.isIndexed(property) &&
					!overview.isMapped(property)) {

				types.put(property, overview.getPropertyType(property));
				titles.put(property, beanView.titleFor(property));
			}
		}
		
		return new Morphicness() {
			
			@Override
			public String[] getNames() {
				return types.keySet().toArray(new String[types.size()]);
			}
			
			@Override
			public Class<?> typeOf(String name) {
				return types.get(name);
			}
			
			@Override
			public String titleFor(String name) {
				return titles.get(name);
			}
		};
			
	}	
}

