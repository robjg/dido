package org.oddjob.dido;


/**
 * Provide instructions for what to do after a {@link DataNode} has 
 * processed data.
 * 
 * @author rob
 *
 * @param <PROVIDE_IN> The type of data the data node provides for children.
 */
public class WhereNextIn<
		PROVIDE_IN extends DataIn> 
extends WhereNext<
	DataNode<PROVIDE_IN, ?, ?, ?>,
	PROVIDE_IN>
		{

	public WhereNextIn() {
		this(null, null);
	}
	
	public WhereNextIn(DataNode<PROVIDE_IN, ?, ?, ?>[] down, 
			PROVIDE_IN childData) {
		super(down, childData);
	}
	
	public DataNode<PROVIDE_IN, ?, ?, ?>[] getChildren() {
		return (DataNode<PROVIDE_IN, ?, ?, ?>[]) super.getChildren();
	}
	
	public PROVIDE_IN getChildData() {
		return super.getChildData();
	}	
}
