package org.oddjob.dido;

import java.util.List;

import org.oddjob.dido.bio.DataBindingIn;


public interface Layout extends ReaderFactory {

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
	public void bind(DataBindingIn bin);
	
	public void reset();
	
	public List<Layout> childLayouts();
	
}
