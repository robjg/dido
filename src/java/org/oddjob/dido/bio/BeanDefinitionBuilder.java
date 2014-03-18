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
import org.oddjob.dido.morph.MorphProvider;
import org.oddjob.dido.morph.Morphable;

/**
 * Helper class to create the Magic Bean Class and Bean View for a binding
 * where the type has not been specified.
 * <p>
 * This helper will replace invalid property names to ensure the binding
 * can always take place without an exception.
 * 
 * @see Morphable
 * @see MorphProvider
 * 
 * @author rob
 *
 */
public class BeanDefinitionBuilder {

	/** The characters in a field name that need to be replace. */
	private final static Set<Character> special = 
			new HashSet<Character>(Arrays.asList('[', ']', '(', ')', '.'));

	private final AtomicInteger instance = new AtomicInteger();
	
	/** Property to label. */
	private final Map<String, String> properties = 
			new LinkedHashMap<String, String>();
	
	/** To create the magic bean. */
	private final MagicBeanClassCreator classCreator = 
			new MagicBeanClassCreator("BeanBinding" + 
					instance.incrementAndGet());

	/**
	 * Add a property.
	 * 
	 * @param name
	 * @param type
	 */
	public void addProperty(String name, Class<?> type) {
		
		String property = replaceSpecialCharacters(name);
		addProperty(property, name, type);
	}
	
	/**
	 * Add a property of the given label.
	 * 
	 * @param property
	 * @param label
	 * @param type
	 */
	public void addProperty(String property, String label, Class<?> type) {
		
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
	
	/**
	 * Create the type.
	 * 
	 * @return
	 */
	public ArooaClass createType() {
		return classCreator.create();
	}
	
	/**
	 * Create the bean view.
	 * 
	 * @return
	 */
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
