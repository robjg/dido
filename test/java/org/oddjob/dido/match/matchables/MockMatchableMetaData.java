package org.oddjob.dido.match.matchables;

import org.oddjob.dido.match.MockMatchDefinition;

public class MockMatchableMetaData extends MockMatchDefinition
implements MatchableMetaData {

	@Override
	public Class<?> getPropertyType(String name) {
		throw new RuntimeException("Unexpected");
	}	
}
