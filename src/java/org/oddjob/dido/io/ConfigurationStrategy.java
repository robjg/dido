package org.oddjob.dido.io;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.dido.DataNode;

public interface ConfigurationStrategy {

	public void configureInitially(ArooaSession session, 
			DataNode<?, ?, ?, ?> topNode);
	
	public void configureEvery(ArooaSession session, 
			DataNode<?, ?, ?, ?> dataNode);
}
