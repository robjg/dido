package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.Binding;

/**
 * Base class that provides common implementation for {@link Layout}s.
 * 
 * @author rob
 *
 */
abstract public class LayoutNode implements Layout {

	private static final Logger logger = Logger.getLogger(LayoutNode.class);
	
	/** The binding, if set. */
	private Binding binding;
	
	/**
	 * @oddjob.property 
	 * @oddjob.description The name of this layout. The name is the main
	 * identification of a layout node. It is commonly used by bindings to
	 * associate with the property name of a Java Object.
	 * @oddjob.required No.
	 */
	private String name;
	
	/** The children. */
	private final List<Layout> children = 
			new ArrayList<Layout>();		
	
	/**
	 * Setter for name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Layout> childLayouts() {
		return children;
	}
	
	/**
	 * Provide sub classes with a way of setting children.
	 * 
	 * @param index The 0 based index.
	 * @param child The child. If null the child for the given index will
	 * be removed.
	 */
	protected void addOrRemoveChild(int index, Layout child) {
		if (child == null) {
			children.remove(index);
		}
		else {
			children.add(index, child);
		}
	}
	
	@Override
	public void setBinding(Binding binding) {
		this.binding = binding;
	}
	
	/**
	 * Allow access to binding for sub classes.
	 * 
	 * @return The binding. Null if no binding.
	 */
	protected Binding binding() {
		return binding;
	}
	
	@Override
	public void reset() {
		for (Layout child : children) {
			child.reset();
		}
	}
	
	/**
	 * Provide the next reader to sub classes. This will be a reader from
	 * a {@link Binding} if one is set or a reader of child nodes.
	 * 
	 * @param dataIn The data the next reader will read.
	 * 
	 * @return The next reader. Will not be null.
	 * 
	 * @throws DataException
	 */
	protected DataReader nextReaderFor(final DataIn dataIn) throws DataException {
		
		DataReader nextReader;
		if (binding() == null) {
			nextReader = new ChildReader(childLayouts(), dataIn);
		}
		else {
			nextReader = binding().readerFor(this, dataIn);
		}
		
		logger.trace("[" + this + "] next reader is [" + nextReader + "]");		
		
		return nextReader;
	}
	
	/**
	 * Provide the next writer to sub classes. This will be a writer to
	 * a {@link Binding} if one is set or a writer to child nodes.
	 * 
	 * @param dataOut The data the next writer will write to.
	 * 
	 * @return The next writer. Never null.
	 *
	 * @throws DataException
	 */
	protected DataWriter nextWriterFor(final DataOut dataOut) throws DataException {
		
		DataWriter nextWriter;
		if (binding() == null) {
			nextWriter = new ChildWriter(childLayouts(), dataOut);
		}
		else {
			nextWriter = binding().writerFor(this, dataOut);
		}

		logger.trace("[" + this + "] next writer is a [" + nextWriter + "].");
		
		return nextWriter;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + 
				(name == null ? "" : ": " + name);
	}
}
