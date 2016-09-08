package org.oddjob.dido.morph;

/**
 * Definition used by something that is {@link Morphable}.
 * <p>
 * Essentially this is an abstraction of the readable properties of a
 * java bean.
 * 
 * @author rob
 *
 */
public interface MorphDefinition {

	/**
	 * Get the names of the child layouts.
	 * 
	 * @return An array. Never null and never containing a null element.
	 */
	public String[] getNames();
	
	/**
	 * Provide the type for the given child element name.
	 * 
	 * @param name The child element name. Must not be null.
	 * @return The class type. Undefined behaviour if the name doesn't exist.
	 */
	public Class<?> typeOf(String name);
	
	/**
	 * Provide the label for the given child element name. The label will
	 * be the element name if not specific label exists.
	 *
	 * @param name The child element name. Must not be null.
	 * @return The label. Undefined behaviour if the name doesn't exist.
	 */
	public String labelFor(String name);
}
