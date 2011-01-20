package org.oddjob.dido.io;

import org.oddjob.dido.DataNode;

/**
 * An event for a {@link LinkableOut}.
 * 
 * @author rob
 *
 */
public class LinkOutEvent {
	
	private final LinkableOut source;
	
	private final DataNode<?, ?, ?, ?> node;
	
	public LinkOutEvent(LinkableOut source, DataNode<?, ?, ?, ?> node) {
		this.source = source;
		this.node = node;
	}
	
	public LinkableOut getSource() {
		return source;
	}
	
	public DataNode<?, ?, ?, ?> getNode() {
		return node;
	}
	
}
