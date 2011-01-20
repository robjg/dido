package org.oddjob.dido.io;

import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.dido.DataNode;

/**
 * A thing, generally a {@link DataNode} that 
 * uses a Class to dynamically create it's structure. 
 * 
 * 
 * @author rob
 *
 */
public interface ClassMorphic {

	public void beFor(ArooaClass arooaClass);
}
