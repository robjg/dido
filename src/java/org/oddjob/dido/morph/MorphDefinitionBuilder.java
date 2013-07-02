package org.oddjob.dido.morph;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MorphDefinitionBuilder {

	private final Map<String, String> nameHeadings =
			new LinkedHashMap<String, String>();
	
	private final Map<String, Class<?>> nameTypes = 
			new HashMap<String, Class<?>>();
	
	public void add(String name, Class<?> type) {
		add(name, name, type);
	}
	
	public void add(String name, String heading, Class<?> type) {

		nameHeadings.put(name, heading);
		nameTypes.put(name, type);
	}
	
	public MorphDefinition build() {
		
		return new MorphDefinition() {
			
			@Override
			public Class<?> typeOf(String name) {
				return nameTypes.get(name);
			}
			
			@Override
			public String titleFor(String name) {
				return nameHeadings.get(name);
			}
			
			@Override
			public String[] getNames() {
				return nameHeadings.keySet().toArray(
						new String[nameHeadings.size()]);
			}
		};
	}
}
