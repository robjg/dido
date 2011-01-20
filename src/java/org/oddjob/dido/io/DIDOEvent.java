package org.oddjob.dido.io;

import org.oddjob.dido.DataNode;

public class DIDOEvent {

	private final NodeWalker source;
	
	private final DataNode<?, ?, ?, ?> node;
	
	public DIDOEvent(NodeWalker source, DataNode<?, ?, ?, ?> node) {
		this.source = source;
		this.node = node;
	}
	
	public NodeWalker getSource() {
		return source;
	}
	
	public DataNode<?, ?, ?, ?> getNode() {
		return node;
	}
	
}
