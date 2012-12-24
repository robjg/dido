package org.oddjob.dido;


public class MockDataNode<
	ACCEPT_IN extends DataIn, PROVIDE_IN extends DataIn,
	ACCEPT_OUT extends DataOut, PROVIDE_OUT extends DataOut>
implements DataNode<
	ACCEPT_IN, PROVIDE_IN,
	ACCEPT_OUT, PROVIDE_OUT> {

	@Override
	public String getName() {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public WhereNextIn<PROVIDE_IN> in(ACCEPT_IN data) 
	throws DataException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public void complete(ACCEPT_IN in) throws DataException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public WhereNextOut<PROVIDE_OUT> 
	out(ACCEPT_OUT outgoing) throws DataException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
		
	public void flush(ACCEPT_OUT data, PROVIDE_OUT childData) 
	throws DataException {
		throw new RuntimeException("Unexpected from " + getClass());		
	}
	
	@Override
	public void complete(ACCEPT_OUT out) throws DataException {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
