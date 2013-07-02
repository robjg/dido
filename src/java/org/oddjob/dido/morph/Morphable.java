package org.oddjob.dido.morph;

import org.oddjob.dido.Layout;

/**
 * A thing, generally a {@link Layout} that 
 * can dynamically create it's structure. 
 * 
 * 
 * @author rob
 *
 */
public interface Morphable {

	public Runnable morphInto(MorphDefinition morphicness);
	
}
