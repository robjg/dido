package org.oddjob.dido;

/**
 * Base class for an object that is a {@link Stencil}. Also contains
 * support for {@link DataNode}s but that isn't expressed in the interface
 * because the signature would bet really messy.
 * 
 * @author rob
 *
 * @param <T>
 */
abstract public class AbstractStencil<T, 
		ACCEPTS_IN extends DataIn, CHILD_IN extends DataIn, 
		ACCEPTS_OUT extends DataOut, CHILD_OUT extends DataOut> 
implements Stencil<T>,
		DataNode<ACCEPTS_IN, CHILD_IN, 
			ACCEPTS_OUT, CHILD_OUT> {
	
	private String name;
	
	/** The stencils value. */
	private T value;

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public T value() {
		return value;
	}
	
	@Override
	public void value(T value) {
		this.value = value;
	}
	
	@Override
	public void complete(ACCEPTS_IN in) throws DataException {
	}
	
	@Override
	public void flush(ACCEPTS_OUT data, CHILD_OUT childData) 
	throws DataException {
	}
	
	@Override
	public void complete(ACCEPTS_OUT out) throws DataException {
	}	
	
}
