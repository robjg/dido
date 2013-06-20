package org.oddjob.dido;

public interface MorphMetaData {

	public String[] getNames();
	
	public Class<?> typeOf(String name);
	
	public String titleFor(String name);
}
