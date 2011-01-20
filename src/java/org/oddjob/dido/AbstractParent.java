package org.oddjob.dido;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.utils.ListSetterHelper;



abstract public class AbstractParent <
		ACCEPTS_IN extends DataIn, CHILD_IN extends DataIn, 
		ACCEPTS_OUT extends DataOut, CHILD_OUT extends DataOut>
implements SupportsChildren,
		DataNode<ACCEPTS_IN, CHILD_IN, ACCEPTS_OUT, CHILD_OUT> {

	private String name;
	
	private final List<DataNode<CHILD_IN, ? , CHILD_OUT, ?>> children = 
		new ArrayList<DataNode<CHILD_IN, ? , CHILD_OUT, ?>>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@ArooaComponent
	public void setIs(int index, 
			DataNode<CHILD_IN, ? , CHILD_OUT, ?> child) {
		new ListSetterHelper<DataNode<CHILD_IN, ? , CHILD_OUT, ?>>(
				children).set(index, child);		
	}

	public DataNode<CHILD_IN, ? , CHILD_OUT, ?> getIs(int index) {
		return children.get(index);
	}
	
	protected boolean hasChildren() {
		return !children.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public DataNode<CHILD_IN, ? , CHILD_OUT, ?>[] childrenToArray() {
		return children.toArray(new DataNode[children.size()]);
	}

	@Override
	public void complete(ACCEPTS_IN in) throws DataException {
	}
	
	public void flush(ACCEPTS_OUT data, CHILD_OUT childData) throws 
	DataException {
		
	}
	
	@Override
	public void complete(ACCEPTS_OUT out) throws DataException {
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
