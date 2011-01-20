package org.oddjob.dido.bio;

import org.oddjob.dido.io.DataLinkOut;
import org.oddjob.dido.io.LinkableOut;

public class MockLinkableOut implements LinkableOut {

	public void setLinkOut(org.oddjob.dido.DataNode<?,?,?,?> node, 
			DataLinkOut link) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
