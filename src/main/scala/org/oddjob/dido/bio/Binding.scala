package org.oddjob.dido.bio

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;

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
trait Binding {

	/**
	 * Provide an {@link DataReader} for the {@Layout} that may provide a 
	 * Java Object. 
	 * <p>
	 * Note that the returned reader will be called repeatedly for the same 
	 * data until it returns null. 
	 * 
	 * @param boundLayout The {{{Layout}}} being bound to.
	 * @param dataIn The current data being read. This is provided so that
	 * it can be passed to children of the {{{Layout}}} if requied.
	 * 
	 * @return The DataReader. Will not be null.
	 * 
	 * @throws DataException
	 */
	@throws(classOf[DataException])
	def readerFor(boundLayout: Layout, dataIn: DataIn): DataReader

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
	@throws(classOf[DataException])
	def writerFor(boundLayout: Layout, dataOut: DataOut): DataWriter

	/**
	 * Reset the binding. A binding may have altered the {@link Layout} it 
	 * is bound to, added children to a {@link Morphable} for instance. This
	 * provides the opportunity to undo those changes.
	 */
	def free(): Unit;
}
