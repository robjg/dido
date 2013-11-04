package org.oddjob.dido.morph;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper to build {@link MorphDefinition}s.
 * 
 * @author rob
 *
 */
public class MorphDefinitionBuilder {

	private final Map<String, String> nameHeadings =
			new LinkedHashMap<String, String>();
	
	private final Map<String, Class<?>> nameTypes = 
			new HashMap<String, Class<?>>();
	
	/**
	 * Add a name and a label that is the same as the name.
	 * 
	 * @param name
	 * @param type
	 */
	public void add(String name, Class<?> type) {
		add(name, name, type);
	}
	
	/**
	 * Add a name and a label.
	 * 
	 * @param name
	 * @param label
	 * @param type
	 */
	public void add(String name, String label, Class<?> type) {

		nameHeadings.put(name, label);
		nameTypes.put(name, type);
	}
	
	/**
	 * Build the {@link MorphDefinition}.
	 * 
	 * @return
	 */
	public MorphDefinition build() {
		
		return new MorphDefinition() {
			
			@Override
			public Class<?> typeOf(String name) {
				return nameTypes.get(name);
			}
			
			@Override
			public String labelFor(String name) {
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
