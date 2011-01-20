package org.oddjob.dido;

import org.oddjob.arooa.ArooaSession;

public class DataPlan<
	ACCEPT_IN extends DataIn, PROVIDE_IN extends DataIn,
	ACCEPT_OUT extends DataOut, PROVIDE_OUT extends DataOut> {

	private final ArooaSession session;
	
	private final DataNode
	<ACCEPT_IN, PROVIDE_IN, ACCEPT_OUT, PROVIDE_OUT> topNode;

	public DataPlan(ArooaSession session,
			DataNode<ACCEPT_IN, PROVIDE_IN, ACCEPT_OUT, PROVIDE_OUT> topNode) {
		this.session = session;
		this.topNode = topNode;
	}

	public ArooaSession getSession() {
		return session;
	}

	public DataNode<ACCEPT_IN, PROVIDE_IN, ACCEPT_OUT, PROVIDE_OUT> getTopNode() {
		return topNode;
	}
}
