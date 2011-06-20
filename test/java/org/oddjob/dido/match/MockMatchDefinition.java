package org.oddjob.dido.match;


public class MockMatchDefinition implements MatchDefinition{

	@Override
	public Iterable<String> getKeyProperties() {
		throw new RuntimeException("Unexpected.");
	}
	
	@Override
	public Iterable<String> getValueProperties() {
		throw new RuntimeException("Unexpected.");
	}
	
	@Override
	public Iterable<String> getOtherProperties() {
		throw new RuntimeException("Unexpected.");
	}
}
