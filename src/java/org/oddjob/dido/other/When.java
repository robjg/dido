package org.oddjob.dido.other;

import org.oddjob.dido.AbstractParent;
import org.oddjob.dido.BoundedDataNode;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Selectable;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;


public class When<ACCEPTS_IN extends DataIn, ACCEPTS_OUT extends DataOut> 
extends AbstractParent<ACCEPTS_IN, ACCEPTS_IN, ACCEPTS_OUT, ACCEPTS_OUT>
implements
		BoundedDataNode<ACCEPTS_IN, ACCEPTS_IN, ACCEPTS_OUT, ACCEPTS_OUT>,
		Selectable,
		CaseCondition<String> {

	private String value;
	
	private boolean selected;
	
	public void begin(ACCEPTS_IN in) {
		DataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?>[] children = childrenToArray();
		
		for (DataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?> child : children) {
			if (child instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?>) child).begin(in);
			}
		}
	}
	
	@Override
	public WhereNextIn<ACCEPTS_IN> in(ACCEPTS_IN data) throws DataException {

		return new WhereNextIn<ACCEPTS_IN>(childrenToArray(), data);
	}
	
	public void end(ACCEPTS_IN in) {
		DataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?>[] children = childrenToArray();
		
		for (DataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?> child : children) {
			if (child instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?>) child).end(in);
			}
		}
	}
	

	public void begin(ACCEPTS_OUT out) {
		DataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?>[] children = childrenToArray();
		
		for (DataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?> child : children) {
			if (child instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?>) child).begin(out);
			}
		}		
	}
	
	@Override
	public WhereNextOut<ACCEPTS_OUT> out(ACCEPTS_OUT data) throws DataException {
		
		// Reset for next time.
		selected = false;
		
		return new WhereNextOut<ACCEPTS_OUT>(childrenToArray(), data);
	}
	
	public void end(ACCEPTS_OUT out) {
		DataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?>[] children = childrenToArray();
		
		for (DataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?> child : children) {
			if (child instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<ACCEPTS_IN, ?, ACCEPTS_OUT, ?>) child).end(out);
			}
		}		
	}
		
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean evaluate(String against) {
		return value.equals(against);
	}

	@Override
	public String evaluateOut() {
		if (selected) {
			return value;
		}
		else {
			return null;
		}
	}
	
	@Override
	public Class<String> getType() {
		return String.class;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
