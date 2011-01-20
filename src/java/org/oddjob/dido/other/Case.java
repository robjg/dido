package org.oddjob.dido.other;

import org.oddjob.dido.AbstractParent;
import org.oddjob.dido.BoundedDataNode;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Stencil;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;

/**
 * 
 * @author rob
 *
 * @param <TYPE>
 * @param <IN>
 * @param <OUT>
 */
public class Case <TYPE, IN extends DataIn, OUT extends DataOut>
extends AbstractParent<IN, IN, OUT, OUT>
implements Changeable<TYPE>{

	private TYPE value;
	
	private boolean initialised;
	
	public void initialise(IN in) {
		DataNode<IN, ?, OUT, ?>[] children = childrenToArray();
		
		if (children.length < 1) {
			throw new NullPointerException();
		}
		
		for (int i = 0; i < children.length; ++i) {
			
			if (children[i] instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<IN, ?, OUT, ?>) children[i]).begin(in);
			}
		}
	}
	
	@Override
	public WhereNextIn<IN> in(IN data) throws DataException {

		if (!initialised) {
			initialise(data);
			initialised = true;
		}
		
		DataNode<IN, ?, OUT, ?>[] children = childrenToArray();
		
		DataNode<IN, ?, OUT, ?> descriminator = children[0];
		
		descriminator.in(data);
		
		this.value = ((Stencil<TYPE>) descriminator).value();
		
		DataNode<IN, ?, OUT, ?>[] chosen = evaluate();
		
		if (chosen == null) {
			return new WhereNextIn<IN>();
		}
		else {
			return new WhereNextIn<IN>(chosen, data);
		}
	}
	
	private DataNode<IN, ?, OUT, ?>[] evaluate() {
		DataNode<IN, ?, OUT, ?>[] children = childrenToArray();
		
		DataNode<IN, ?, OUT, ?>[] result = (DataNode<IN, ?, OUT, ?>[])
			new DataNode[1];
		
		for (int i = 1; i < children.length; ++i) {
			DataNode<IN, ?, OUT, ?> child = children[i];
			
			if (((CaseCondition<TYPE>) child).evaluate(value)) {
				result[0] = child;
				return result;
			}
		}
		return null;
	}
	
	@Override
	public void complete(IN in) {
		
		for (DataNode<IN, ?, OUT, ?> child : childrenToArray()) {
			if (child instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<IN, ?, OUT, ?>) child).end(in);
			}
		}
		
		initialised = false;
	}
	
	public void initialise(OUT out) {
		DataNode<IN, ?, OUT, ?>[] children = childrenToArray();
		
		if (children.length < 1) {
			throw new NullPointerException();
		}
		
		for (int i = 0; i < children.length; ++i) {
			
			if (children[i] instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<IN, ?, OUT, ?>) children[i]).end(out);
			}
		}
	}
	
	@Override
	public WhereNextOut<OUT> out(OUT data) throws DataException {
		
		if (!initialised) {
			initialise(data);
			initialised = true;
		}
		
		DataNode<IN, ?, OUT, ?>[] children = childrenToArray();
		
		DataNode<IN, ?, OUT, ?> descriminator = children[0];
		
		DataNode<IN, ?, OUT, ?>[] chosen = evaluateOut();
		
		if (chosen == null) {
			return new WhereNextOut<OUT>();
		}
		else {
			((Stencil<TYPE>) descriminator).value(value);
			
			descriminator.out(data);
			
			return new WhereNextOut<OUT>(chosen, data);
		}
	}

	private DataNode<IN, ?, OUT, ?>[] evaluateOut() {
		DataNode<IN, ?, OUT, ?>[] children = childrenToArray();
		
		DataNode<IN, ?, OUT, ?>[] result = (DataNode<IN, ?, OUT, ?>[])
			new DataNode[1];
		
		for (int i = 1; i < children.length; ++i) {
			DataNode<IN, ?, OUT, ?> child = children[i];
			
			TYPE value = ((CaseCondition<TYPE>) child).evaluateOut();
			
			if (value != null) {
				result[0] = child;
				this.value = value;
				return result;
			}
			
		}
		return null;
	}
	
	@Override
	public void complete(OUT out) {
		
		for (DataNode<IN, ?, OUT, ?> child : childrenToArray()) {
			if (child instanceof BoundedDataNode<?, ?, ?, ?>) {
				((BoundedDataNode<IN, ?, OUT, ?>) child).end(out);
			}
		}
		
		initialised = false;
	}
	
	@Override
	public void changeValue(TYPE value) {
		((Stencil<TYPE>) childrenToArray()[0]).value(value);
	}

}
