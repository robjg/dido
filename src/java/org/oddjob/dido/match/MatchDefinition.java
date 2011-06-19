package org.oddjob.dido.match;

/**
 * Define the properties for matching two things.
 * <p>
 * 
 * @author Rob
 *
 */
public interface MatchDefinition {

	/**
	 * The key property names. These properties decide if two things 
	 * can be compared.
	 * 
	 * @return The names of the key properties.
	 */
	public Iterable<String> getKeyProperties();
	
	/**
	 * The names of the properties of the values that will be compared.
	 * 
	 * @return The names of the properties for comparison.
	 */
	public Iterable<String> getValueProperties();
		
		
	/**
	 * Other properties that wont be used in the match, but which
	 * might be required for information on results. Inserted time,
	 * or Operator Id for instance.
	 * 
	 * @return The names of other properties.
	 */
	public Iterable<String> getOtherProperties();
	
}
