package org.oddjob.dido.io;

import org.oddjob.dido.DataNode;
import org.oddjob.dido.Morphicness;

/**
 * A thing, generally a {@link DataNode} that 
 * can dynamically create it's structure. 
 * 
 * 
 * @author rob
 *
 */
public interface ClassMorphic {

	public Runnable beFor(Morphicness morphicness);
}
