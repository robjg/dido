package org.oddjob.dido;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.utils.ListSetterHelper;

/**
 * Base functionality for being a node that {@link SupportsChildren}
 * and is a {@link Stencil}.
 * 
 * @author rob
 *
 * @param <T> The Stencil type.
 * @param <CHILD_IN> The type the children accepts in.
 * @param <CHILD_OUT> The type the children accepts out.
 */
abstract public class AbstractParentStencil<T, 
	ACCEPTS_IN extends DataIn, CHILD_IN extends DataIn, 
	ACCEPTS_OUT extends DataOut, CHILD_OUT extends DataOut> 
extends AbstractStencil<T, ACCEPTS_IN, CHILD_IN, ACCEPTS_OUT, CHILD_OUT>
implements SupportsChildren {

	private final List<DataNode<CHILD_IN, ?, CHILD_OUT, ?>> children = 
		new ArrayList<DataNode<CHILD_IN, ?, CHILD_OUT, ?>>();
	
	@ArooaComponent
	public void setIs(int index, 
			DataNode<CHILD_IN, ?, CHILD_OUT, ?> child) {
		new ListSetterHelper<DataNode<CHILD_IN, ? , CHILD_OUT, ?>>(
				children).set(index, child);		
	}

	public DataNode<CHILD_IN, ?, CHILD_OUT, ?> getIs(int index) {
		return children.get(index);
	}
	
	protected boolean hasChildren() {
		return !children.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public DataNode<CHILD_IN, ?, CHILD_OUT, ?>[] childrenToArray() {
		return (DataNode<CHILD_IN, ?, CHILD_OUT, ?>[]) children.toArray(
				new DataNode[children.size()]);
	}
	
	protected void fireChildrenBegin(CHILD_IN in) {
		DataNode<CHILD_IN, ?, CHILD_OUT, ?>[] children = childrenToArray();
		
		for (int i = 0; i < children.length; ++i) {
			if (children[i] instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<CHILD_IN, ?, CHILD_OUT, ?>) 
						children[i]).begin(in);
			}
		}
	}
	
	protected void fireChildrenEnd(CHILD_IN in) {
		for (DataNode<CHILD_IN, ?, CHILD_OUT, ?> child : childrenToArray()) {
			if (child instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<CHILD_IN, ?, CHILD_OUT, ?>) child).end(in);
			}
		}
	}
	
	protected void fireChildrenBegin(CHILD_OUT out) {
		DataNode<CHILD_IN, ?, CHILD_OUT, ?>[] children = childrenToArray();
		
		for (int i = 0; i < children.length; ++i) {
			if (children[i] instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<CHILD_IN, ?, CHILD_OUT, ?>) 
						children[i]).begin(out);
			}
		}
	}
	
	protected void fireChildrenEnd(CHILD_OUT out) {
		for (DataNode<CHILD_IN, ?, CHILD_OUT, ?> child : childrenToArray()) {
			if (child instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<CHILD_IN, ?, CHILD_OUT, ?>) child).end(out);
			}
		}
	}
}
