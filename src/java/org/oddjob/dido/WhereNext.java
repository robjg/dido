package org.oddjob.dido;


/**
 * Provide instructions on what to do after processing a {@link DataNode}.
 * 
 * @see WhereNextIn, WhereNextOut.
 *  
 * @author rob
 *
 * @param <CHILD_TYPE>
 * @param <CHILD_ACCEPTS>
 * @param <AFTER_TYPE>
 */
public class WhereNext<CHILD_TYPE, CHILD_ACCEPTS> {

	private final CHILD_ACCEPTS childData;
	
	private final CHILD_TYPE[] children;

	public WhereNext(CHILD_TYPE[] down, CHILD_ACCEPTS childData) {
		this.children = down;
		this.childData = childData;
	}
	
	public CHILD_TYPE[] getChildren() {
		return children;
	}
	
	public CHILD_ACCEPTS getChildData() {
		return childData;
	}	
}
