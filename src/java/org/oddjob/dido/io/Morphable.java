package org.oddjob.dido.io;

import org.oddjob.dido.Layout;
import org.oddjob.dido.MorphMetaData;

/**
 * A thing, generally a {@link Layout} that 
 * can dynamically create it's structure. 
 * 
 * 
 * @author rob
 *
 */
public interface Morphable {

	public Runnable morphInto(MorphMetaData morphicness);
	
	public MorphMetaData morphOf();
}
