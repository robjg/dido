package org.oddjob.dido.io;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.SupportsChildren;

public enum ConfigurationType implements ConfigurationStrategy {

	NEVER() {
		@Override
		public void configureInitially(ArooaSession session,
				DataNode<?, ?, ?, ?> topNode) {
			// Nothing to do here.
		}
		@Override
		public void configureEvery(ArooaSession session,
				DataNode<?, ?, ?, ?> dataNode) {
			// Nothing to do here.
		}
	},
	INITIAL() {
		@Override
		public void configureInitially(ArooaSession session,
				DataNode<?, ?, ?, ?> topNode) {
			session.getComponentPool().configure(topNode);
			if (topNode instanceof SupportsChildren) {
				for (DataNode<?, ?, ?, ?> child : 
					((SupportsChildren) topNode).childrenToArray()) {
					configureInitially(session, child);
				}
			}
		}
		@Override
		public void configureEvery(ArooaSession session,
				DataNode<?, ?, ?, ?> dataNode) {
			// Nothing to do here.
		}
	},
	EVERY() {
		@Override
		public void configureInitially(ArooaSession session,
				DataNode<?, ?, ?, ?> topNode) {
			// Nothing to do here.
		}
		@Override
		public void configureEvery(ArooaSession session,
				DataNode<?, ?, ?, ?> dataNode) {
			session.getComponentPool().configure(dataNode);
		}
	};
	
}
