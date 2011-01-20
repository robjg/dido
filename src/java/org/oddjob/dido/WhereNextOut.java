package org.oddjob.dido;

/**
 * Provide instructions for what to do after a {@link DataNode} has 
 * processed data.
 * 
 * 
 * @author rob
 *
 * @param <PROVIDE_OUT>
 */
public class WhereNextOut<PROVIDE_OUT extends DataOut> 
extends WhereNext<
		DataNode<?, ?, PROVIDE_OUT, ?>, 
		PROVIDE_OUT> {

	public WhereNextOut() {
		this(null, null);
	}
	
	public WhereNextOut(DataNode<?, ?, PROVIDE_OUT, ?>[] down, 
			PROVIDE_OUT childData) {
		super(down, childData);
	}
	
}
