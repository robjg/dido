package org.oddjob.dido.layout;

import java.util.Map;

import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.SimpleBeanRegistry;
import org.oddjob.dido.Layout;

public class LayoutDirectoryFactory {

	private final PropertyAccessor accessor;
	
	private final ArooaConverter converter;
	
	public LayoutDirectoryFactory(PropertyAccessor accessor,
			ArooaConverter converter) {
		this.accessor = accessor;
		this.converter = converter;
	}
	
	public BeanDirectory createFrom(Layout root) {
		
		return createFrom(new LayoutsByName(root).getAll());
		
	}
	
	public BeanDirectory createFrom(Map<String, Layout> layouts) {
		
		SimpleBeanRegistry registry = new SimpleBeanRegistry(accessor, converter);
		
		for (Map.Entry<String, Layout> entry : layouts.entrySet()) {
			
			registry.register(entry.getKey(), entry.getValue());
		}
		
		return registry;
	}
}
