package org.oddjob.dido;

/**
 * A thing capable of reading a particular type of data and writing a
 * particular type of data.
 * <p>
 * An instance will probably be part of a hierarchy of nodes, and may have
 * children to which it will pass data object to be read or written to.
 * 
 * @author rob
 *
 * @param <ACCEPT_IN> The type of data this can read in.
 * @param <PROVIDE_IN> The type of data this passes to children to read in.
 * @param <ACCEPT_OUT> The type of data this can write out.
 * @param <PROVIDE_OUT> Type type of data this passes to children to write out.
 */
public interface DataNode<
	ACCEPT_IN extends DataIn, PROVIDE_IN extends DataIn,
	ACCEPT_OUT extends DataOut, PROVIDE_OUT extends DataOut> {

	
	/**
	 * The name of the node. May be null.
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * Reads data into the value.
	 * 
	 * @param din Data In.
	 * 
	 * @throws DataException
	 */
	public 
	WhereNext<
		DataNode<PROVIDE_IN, ?, ?, ?>, 
		PROVIDE_IN>
	in(ACCEPT_IN din) 
	throws DataException;

	/**
	 * Called when there is no more data to read in for 
	 * the current node.
	 * 
	 * @param din Data In.
	 * 
	 * @throws DataException
	 */
	public void complete(ACCEPT_IN din) throws DataException;
	
	/**
	 * Writes data from the value.
	 * 
	 * @param dout Data Out.
	 * 
	 * @throws DataException
	 */
	public 
	WhereNext<
		DataNode<?, ?, PROVIDE_OUT, ?>, 
		PROVIDE_OUT> 
	out(ACCEPT_OUT dout)
	throws DataException;
	
	/**
	 * Called when this node and all it's children have been 
	 * processed.
	 * 
	 * @param dout Data Out.
	 * @param childDout Child Data Out.
	 * 
	 * @throws DataException
	 */
	public void flush(ACCEPT_OUT dout, PROVIDE_OUT childDout)
	throws DataException;
	
	
	/**
	 * Called when there is no more data to write for
	 * the this node.
	 * 
	 * @param dout Data Out.
	 * 
	 * @throws DataException
	 */
	public void complete(ACCEPT_OUT dout) throws DataException;
	
}
