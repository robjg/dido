package org.oddjob.dido;

public interface Morphicness {

	public String[] getNames();
	
	public Class<?> typeOf(String name);
	
	public String titleFor(String name);
}
