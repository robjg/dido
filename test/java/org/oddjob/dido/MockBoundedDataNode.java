package org.oddjob.dido;


public class MockBoundedDataNode<
	ACCEPT_IN extends DataIn, PROVIDE_IN extends DataIn,
	ACCEPT_OUT extends DataOut, PROVIDE_OUT extends DataOut>
extends MockDataNode<
	ACCEPT_IN, PROVIDE_IN,
	ACCEPT_OUT, PROVIDE_OUT> 
implements BoundedDataNode<
	ACCEPT_IN, PROVIDE_IN,
	ACCEPT_OUT, PROVIDE_OUT> {

	@Override
	public void begin(ACCEPT_IN in) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public void end(ACCEPT_IN in) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public void begin(ACCEPT_OUT out) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	@Override
	public void end(ACCEPT_OUT out) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
		
}
