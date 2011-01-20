package org.oddjob.dido;

/**
 * For DataNodes that are bounded by something like a header of trailer. May
 * also be used for node that need to remember state, like the number of
 * lines read.
 * 
 * 
 * @author rob
 *
 * @param <ACCEPT_IN>
 * @param <PROVIDE_IN>
 * @param <ACCEPT_OUT>
 * @param <PROVIDE_OUT>
 */
public interface BoundedDataNode<
		ACCEPT_IN extends DataIn, PROVIDE_IN extends DataIn,
		ACCEPT_OUT extends DataOut, PROVIDE_OUT extends DataOut>
extends DataNode<ACCEPT_IN, PROVIDE_IN, ACCEPT_OUT, PROVIDE_OUT>{

	/**
	 * Called by the reader before the node or any of it's siblings have 
	 * read data in.
	 * 
	 * @param in
	 */
	public void begin(ACCEPT_IN in);
	
	/**
	 * Called after a parent has been read for the last time.
	 * 
	 * @param in
	 */
	public void end(ACCEPT_IN in);
	
	/**
	 * Called by a writer before the node or any of it's siblings have
	 * written data out.
	 * 
	 * @param out
	 */
	public void begin(ACCEPT_OUT out);
	
	/**
	 * Called after a parent has written data for the last time.
	 * 
	 * @param out
	 */
	public void end(ACCEPT_OUT out);
}
