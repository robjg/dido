package org.oddjob.dido;

import org.oddjob.arooa.ArooaSession;

public class DataPlan {

	private final ArooaSession session;
	
	private final Layout topNode;

	public DataPlan(ArooaSession session,
			Layout topNode) {
		this.session = session;
		this.topNode = topNode;
	}

	public ArooaSession getSession() {
		return session;
	}

	public Layout getTopNode() {
		return topNode;
	}
}
