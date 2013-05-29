package org.oddjob.dido.io;

import org.oddjob.dido.DataNode;
import org.oddjob.dido.ValueNode;


/**
 * Provide a link for data between with a {@link LinkableIn}
 * 
 * @author rob
 *
 */
public interface DataLinkIn {

	/**
	 * Called by a {@link LinkableIn} after a {@link DataNode} has been
	 * visited. If the {@linkplain DataNode} is a {@link ValueNode} that has
	 * no children, the stencil will have it's value populated.
	 * 
	 * @param event
	 * @return
	 */
	public LinkInControl dataIn(LinkInEvent event);
	
	/**
	 * Called after all the {@link DataNode}s of this link have been called.
	 * For instance if this was a link to a Lines node, then the method
	 * will be called after the last line has been read and processed.
	 * 
	 * @param event
	 */
	public void lastIn(LinkInEvent event);
	
}
