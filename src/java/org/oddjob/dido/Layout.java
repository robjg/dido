package org.oddjob.dido;

import java.util.List;

import org.oddjob.dido.bio.DataBinding;


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
	 * @param bin
	 */
	public void bind(DataBinding bindings);
	
	
	public List<Layout> childLayouts();
	
	
	public void close();
}
