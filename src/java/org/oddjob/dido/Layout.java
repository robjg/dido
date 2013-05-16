package org.oddjob.dido;

import java.util.List;

import org.oddjob.dido.bio.Binding;


public interface Layout extends DataReaderFactory, DataWriterFactory {

	/**
	 * The name of the node. May be null.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * 
	 * @param binding
	 */
	public void bind(Binding bindings);
	
	
	public List<Layout> childLayouts();
	
	
	public void close();
}
