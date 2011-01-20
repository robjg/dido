package org.oddjob.dido.io;

import org.oddjob.dido.DataNode;

/**
 * Events for link via a {@link LinkableIn}.
 * 
 * @author rob
 *
 */
public class LinkInEvent {
	
	private final LinkableIn source;
	
	private final DataNode<?, ?, ?, ?> node;
	
	public LinkInEvent(LinkableIn source, DataNode<?, ?, ?, ?> node) {
		this.source = source;
		this.node = node;
	}
	
	public LinkableIn getSource() {
		return source;
	}
	
	public DataNode<?, ?, ?, ?> getNode() {
		return node;
	}
	
}
