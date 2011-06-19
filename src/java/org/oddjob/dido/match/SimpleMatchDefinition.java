package org.oddjob.dido.match;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Simple definition of an {@link MatchDefinition}.
 * 
 * @author rob
 *
 */
public class SimpleMatchDefinition implements MatchDefinition {

	/** Key property names. */
	private final Iterable<String> keyProperties;

	/** Values for comparison property names. */
	private final Iterable<String> valueProperties;
	
	/** Other property names. */
	private final Iterable<String> otherProperties;
	
	/**
	 * Constructor.
	 * 
	 * @param keys Array of key property names. May be null. 
	 * @param values Array of value property names. May be null.
	 * @param others Array of other property names. May be null.
	 */
	public SimpleMatchDefinition(String[] keys,
			String[] values, String[] others) {
		
		if (keys == null) {
			this.keyProperties = new ArrayList<String>();
		}
		else {
			this.keyProperties = Arrays.asList(keys);

		}
		
		if (values == null) {
			this.valueProperties = new ArrayList<String>();
		}
		else {
			this.valueProperties = Arrays.asList(values);
		}
	
		if (others == null) {
			this.otherProperties = new ArrayList<String>();
		}
		else {
			this.otherProperties = Arrays.asList(others);
		}
	}	
	
	@Override
    public Iterable<String> getKeyProperties() {
		return keyProperties;
    }
	
	@Override
	public Iterable<String> getValueProperties() {
		return valueProperties;
	}
	
	@Override
	public Iterable<String> getOtherProperties() {
		return otherProperties;
	}
}
