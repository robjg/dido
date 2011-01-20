package org.oddjob.dido;

import org.oddjob.dido.other.Case;

/**
 * Allows a node to be set as selected for output. In a situation where there
 * is a choice of nodes for output (such as within a {@link Case} section)
 * the writer will mark the the selected condition.
 * 
 * @author rob
 *
 */
public interface Selectable {

	/**
	 * Set the node as selected or not.
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected);
}
