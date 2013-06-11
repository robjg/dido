package org.oddjob.dido.io;

import org.oddjob.dido.Layout;
import org.oddjob.dido.Morphicness;

/**
 * A thing, generally a {@link Layout} that 
 * can dynamically create it's structure. 
 * 
 * 
 * @author rob
 *
 */
public interface ClassMorphic {

	public Runnable beFor(Morphicness morphicness);
}
