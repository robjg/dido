package org.oddjob.dido.layout;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.Binding;

public class BindingHelper {

	private final Map<String, Layout> nameToLayout =
			new HashMap<String, Layout>();

	public BindingHelper(Layout layout) {
		
		// LayoutWalker doesn't visit the root node!
		String name = layout.getName();
		if (name != null) {
			nameToLayout.put(name, layout);
		}
		
		new LayoutWalker() {
			@Override
			protected boolean onLayout(Layout layout) {
				String name = layout.getName();
				if (name != null) {
					nameToLayout.put(name, layout);
				}
				
				return true;
			}
		}.walk(layout);
		
	}
	
	public void bind(String name, Binding binding) {
		Layout layout = nameToLayout.get(name);

		if (layout == null) {
			throw new NullPointerException("No Layout to bind to named " + 
					name);
		}
		
		layout.bind(binding);
	}
}
