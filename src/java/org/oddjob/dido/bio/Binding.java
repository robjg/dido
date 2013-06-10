package org.oddjob.dido.bio;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;
import org.oddjob.dido.io.ClassMorphic;

/**
 * Provide the ability to bind data to and from a Java Object. A 
 * {@code Binding} is generally bound to a {@link Layout} that will 
 * call the {@link #extract(Layout, DataIn, boolean)} method during 
 * reading and the {@link #inject(Object, Layout, DataOut)} method during
 * writing.
 * 
 * @author rob
 *
 */
public interface Binding {

	/**
	 * Extract data from the {@Layout} into a Java Object. Note that this 
	 * method will be called repeatedly for the same data until it returns null. During
	 * subsequent calls the {@code revist} flag will be true so that simple bindings
	 * where there is a one Object for one Layout may simply return null
	 * when the revisit flag is null.
	 * 
	 * @param boundLayout The {@code Layout} being bound to.
	 * @param dataIn The current data being read. This is provided so that
	 * it can be passed to children of the {@code Layout} if requied.
	 * @param revist Is this the second visit to this binding for the
	 * same data. True if it is, false otherwise.
	 * 
	 * @return The Object created by this {@code Binding}. Note that on the
	 * will that more data should be read.
	 * 
	 * @throws DataException
	 */
	public Object extract(Layout boundLayout, DataIn dataIn, 
			boolean revist)
	throws DataException;
	
	/**
	 * Inject a Java Object into the {@link Layout} (for a direct binding) or 
	 * {@link DataOut} (normally by calling a child processor).
	 * 
	 * @param object The value to be written.
	 * @param boundLayout The node that this binding is bound to.
	 * @param dataOut The data that will be written to.
	 * 
	 * @return Is more data required. If true then any calling node should
	 * return to client code to be given a new value. If false calling code
	 * should progress on to the next node in the sequence of walking the
	 * layout hierarchy.
	 * 
	 * @throws DataException
	 */
	public boolean inject(Object object, 
			Layout boundLayout, DataOut dataOut)
	throws DataException;
	
	/**
	 * Reset the binding. A binding may have altered the {@link Layout} it 
	 * is bound to, added children to a {@link ClassMorphic} for instance. This
	 * provides the opportunity to undo those changes.
	 */
	public void free();
}
