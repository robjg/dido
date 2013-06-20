package org.oddjob.dido;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.BeanView;
import org.oddjob.arooa.reflect.FallbackBeanView;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Helper class for creating {@link MorphMetaData} from {@link ArooaClass}s
 * and {@link BeanView}s.
 * 
 * @author rob
 *
 */
public class MorphMetaDataFactory {

	private final PropertyAccessor accessor;
	
	public MorphMetaDataFactory(PropertyAccessor accessor) {
		this.accessor = accessor;
	}
		
	public MorphMetaData readableMorphMetaDataFor(ArooaClass arooaClass) {
		return readableMorphMetaDataFor(arooaClass, null);
	}		
		
	public MorphMetaData readableMorphMetaDataFor(
			ArooaClass arooaClass, BeanView beanView ) {

		BeanOverview overview = 
				arooaClass.getBeanOverview(accessor);
						
		if (beanView == null) {
			beanView = new FallbackBeanView(accessor, arooaClass);
		}
		
		final List<String> properties = new ArrayList<String>();

		final Map<String, Class<?>> types = new TreeMap<String, Class<?>>();
		final Map<String, String> titles = new TreeMap<String, String>();
		
		for (String property : beanView.getProperties()) {
				
			if ("class".equals(property)) {
				continue;
			}

			if (overview.hasReadableProperty(property) &&
					!overview.isIndexed(property) &&
					!overview.isMapped(property)) {

				properties.add(property);
				types.put(property, overview.getPropertyType(property));
				titles.put(property, beanView.titleFor(property));
			}
		}
		
		return new MorphMetaData() {
			
			@Override
			public String[] getNames() {
				return properties.toArray(new String[properties.size()]);
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
	
	public MorphMetaData writeableMorphMetaDataFor(ArooaClass arooaClass) {
		return writeableMorphMetaDataFor(arooaClass, null);
	}		
		
	public MorphMetaData writeableMorphMetaDataFor(
			ArooaClass arooaClass, BeanView beanView ) {

		BeanOverview overview = 
				arooaClass.getBeanOverview(accessor);
						
		if (beanView == null) {
			beanView = new FallbackBeanView(accessor, arooaClass);
		}

		final List<String> properties = new ArrayList<String>();

		final Map<String, Class<?>> types = new TreeMap<String, Class<?>>();
		final Map<String, String> titles = new TreeMap<String, String>();
		
		for (String property : beanView.getProperties()) {
				
			if ("class".equals(property)) {
				continue;
			}

			if (overview.hasWriteableProperty(property) &&
					!overview.isIndexed(property) &&
					!overview.isMapped(property)) {

				properties.add(property);
				types.put(property, overview.getPropertyType(property));
				titles.put(property, beanView.titleFor(property));
			}
		}
		
		return new MorphMetaData() {
			
			@Override
			public String[] getNames() {
				return properties.toArray(new String[properties.size()]);
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

