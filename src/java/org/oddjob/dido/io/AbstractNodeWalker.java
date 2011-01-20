package org.oddjob.dido.io;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.DataNode;

/**
 * Base class for things that need to manage {@link DIDOListener}s.
 * 
 * @author rob
 *
 */
public class AbstractNodeWalker implements NodeWalker {

	private final List<DIDOListener> listeners =
		new ArrayList<DIDOListener>();
	
	protected void notifyListenersStart(DataNode<?, ?, ?, ?> node) {
		
		DIDOEvent event = new DIDOEvent(this, node);

		for (DIDOListener listener : this.listeners) {
			listener.startNode(event);
		}
	}
	
	protected void notifyListenersEnd(DataNode<?, ?, ?, ?> node) {
		DIDOEvent event = new DIDOEvent(this, node);
		
		for (DIDOListener listener : this.listeners) {
			listener.endNode(event);
		}
	}
	
	public void addListener(DIDOListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener.");
		}
		
		this.listeners.add(listener);
	}
	
	public void removeListener(DIDOListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener.");
		}
		
		this.listeners.remove(listener);
	}
}
