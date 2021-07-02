package org.oddjob.dido.bio;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.morph.Morphable;

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
	 * Provide an {@link DataReader} for the {@Layout} that may provide a 
	 * Java Object. 
	 * <p>
	 * Note that the returned reader will be called repeatedly for the same 
	 * data until it returns null. 
	 * 
	 * @param boundLayout The {@code Layout} being bound to.
	 * @param dataIn The current data being read. This is provided so that
	 * it can be passed to children of the {@code Layout} if requied.
	 * 
	 * @return The DataReader. Will not be null.
	 * 
	 * @throws DataException
	 */
	public DataReader readerFor(Layout boundLayout, DataIn dataIn)
	throws DataException;
	
	/**
	 * Provide a {@link DataWriter} that may Inject a Java Object 
	 * into the {@link Layout} (for a direct binding) or 
	 * {@link DataOut} (normally by calling a child processor).
	 * 
	 * @param boundLayout The node that this binding is bound to.
	 * @param dataOut The data that will be written to.
	 * 
	 * @return The DataWriter. Must not be null.
	 * 
	 * @throws DataException
	 */
	public DataWriter writerFor(Layout boundLayout, DataOut dataOut)
	throws DataException;
	
	/**
	 * Reset the binding. A binding may have altered the {@link Layout} it 
	 * is bound to, added children to a {@link Morphable} for instance. This
	 * provides the opportunity to undo those changes.
	 */
	public void free();
}
