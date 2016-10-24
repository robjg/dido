package org.oddjob.dido;

import org.oddjob.dido.bio.Binding;

/**
 * A description of how data should be read and written. A {@code Layout} is 
 * hierarchical with children refining the description of the data.
 * <p>
 * {@code Layout}s will often be {@link ValueNode}s that provide a
 * {@link Binding} with data or the facility to write data.
 * <p>
 * {@code Layout}s are stateful. Before being used again the {@link #reset()}
 * method must be called.
 * 
 * @see ValueNode
 * @see Binding
 * 
 * @author rob
 *
 */
trait Layout extends DataReaderFactory with DataWriterFactory {

	/**
	 * The name of the layout. A Layout does not need to have a name but
	 * without a name it can not be identified. The name must be unique
	 * amongst siblings but does not need to be unique within a hierarchy.
	 * 
	 * @return The name. May be null.
	 */
	def getName(): String
	
	/**
	 * Bind a {@link Binding} to this node. Once a Layout is bound it
	 * will not pass control to it's children but will rely on the binding
	 * to process children if the binding so wishes.
	 * 
	 * @param bindings The binding. Setting to null will clear the current
	 * binding.
	 */
	def setBinding(bindings: Binding): Unit;
	
	/**
	 * Provide the children of this Layout.
	 * 
	 * @return
	 */
	def childLayouts(): java.lang.Iterable[Layout]

	/**
	 * Reset this layout. Any state created during the last read or
	 * writes will be cleared.
	 */
	def reset(): Unit;
}
