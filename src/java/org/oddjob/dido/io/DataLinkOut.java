package org.oddjob.dido.io;

/**
 * Provide a link between the structure and the outside world.
 * 
 * @author rob
 *
 */
public interface DataLinkOut {
	
	/**
	 * Called by a writer before the linked node is visited.
	 * 
	 * @param event
	 * @param bean
	 * @return
	 */
	public boolean dataOut(LinkOutEvent event, Object bean);
	
	/**
	 * Called when there are no more nodes of this type in this pass
	 * of the structure.
	 * 
	 * @param event
	 */
	public void lastOut(LinkOutEvent event);
}
