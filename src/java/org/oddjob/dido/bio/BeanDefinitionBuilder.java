package org.oddjob.dido.bio;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.oddjob.arooa.beanutils.MagicBeanClassCreator;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanView;

public class BeanDefinitionBuilder {

	private final static Set<Character> special = 
			new HashSet<Character>(Arrays.asList('[', ']', '(', ')', '.'));

	private final AtomicInteger instance = new AtomicInteger();
	
	private final Map<String, String> properties = 
			new LinkedHashMap<String, String>();
	
	private final MagicBeanClassCreator classCreator = 
			new MagicBeanClassCreator("BeanBinding" + 
					instance.incrementAndGet());
	
	public void addProperty(String name, Class<?> type) {
		
		String property = replaceSpecialCharacters(name);
		addProperty(property, name, type);
	}
	
	protected void addProperty(String property, String label, Class<?> type) {
		
		if (properties.containsKey(property)) {
			addProperty(property + "_", label, type);
			return;
		}
			
		classCreator.addProperty(property, type);
		properties.put(property, label);
	}
	
	// Should be centralised - maybe on ClassCreator?
	protected String replaceSpecialCharacters(String name) {
		
		char[] chars = name.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			if (special.contains(chars[i])) {
				chars[i] = '_';
			}
		}
		return new String(chars);
	}
	
	public ArooaClass createType() {
		return classCreator.create();
	}
	
	public BeanView createBeanView() {
		return new BeanView() {
			
			@Override
			public String titleFor(String property) {
				return properties.get(property);
			}
			
			@Override
			public String[] getProperties() {
				
				return properties.keySet().toArray(
						new String[properties.size()]);
			}
		};
	}
}
