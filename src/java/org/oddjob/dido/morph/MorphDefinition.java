package org.oddjob.dido.morph;

public interface MorphDefinition {

	public String[] getNames();
	
	public Class<?> typeOf(String name);
	
	public String titleFor(String name);
}
