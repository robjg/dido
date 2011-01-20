package org.oddjob.dido.stream;

import org.oddjob.dido.DataDriver;

/**
 * @oddjob.description Lines of text.
 * 
 * @author rob
 *
 */
public class Lines 
extends LinesBase 
implements DataDriver {

	@Override
	public int getRequired() {
		return 0;
	}
}
