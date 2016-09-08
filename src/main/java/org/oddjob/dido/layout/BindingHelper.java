package org.oddjob.dido.layout;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.Binding;

public class BindingHelper {

	private final Map<Layout, Binding> layoutsAndBindings =
			new HashMap<Layout, Binding>();
	
	public void bind(Layout layout, Binding binding) {

		layout.setBinding(binding);
		
		layoutsAndBindings.put(layout, binding);
	}
	
	public void freeAll() {
		
		for (Map.Entry<Layout, Binding> entry : layoutsAndBindings.entrySet()) {
			
			entry.getValue().free();
			entry.getKey().setBinding(null);
		}
		
		layoutsAndBindings.clear();
	}
}
