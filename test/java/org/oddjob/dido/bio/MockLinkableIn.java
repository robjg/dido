package org.oddjob.dido.bio;

import org.oddjob.dido.DataNode;
import org.oddjob.dido.io.DataLinkIn;
import org.oddjob.dido.io.LinkableIn;

public class MockLinkableIn implements LinkableIn {

	@Override
	public void setControlIn(DataNode<?, ?, ?, ?> node, DataLinkIn dataLink) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
