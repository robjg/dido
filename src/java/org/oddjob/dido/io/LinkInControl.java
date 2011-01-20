package org.oddjob.dido.io;

/**
 * Provided by a {@link DataLinkIn} to control it's {@link LinkableIn}
 * 
 * @author rob
 *
 */
public interface LinkInControl {
	
	/**
	 * Get the data object. The reader will return when the control
	 * provides a data object.
	 * 
	 * @return
	 */
	public Object getDataObject();
}
