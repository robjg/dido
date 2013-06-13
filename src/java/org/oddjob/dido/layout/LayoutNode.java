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
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.bio.Binding;


abstract public class LayoutNode implements Layout {

	private static final Logger logger = Logger.getLogger(LayoutNode.class);
	
	private Binding binding;
	
	private String name;
	
	private final List<Layout> children = 
			new ArrayList<Layout>();		
	
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
	
	protected void addOrRemoveChild(int index, Layout child) {
		if (child == null) {
			children.remove(index);
		}
		else {
			children.add(index, child);
		}
	}
	
	@Override
	public void bind(Binding binding) {
		this.binding = binding;
	}
	
	protected Binding binding() {
		return binding;
	}
	
	@Override
	public void reset() {
		for (Layout child : children) {
			child.reset();
		}
	}
	
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
	
	protected DataWriter nextWriterFor(final DataOut dataOut) throws DataException {
		
		DataWriter nextWriter;
		if (binding() == null) {
			nextWriter = new ChildWriter(childLayouts(), 
					((this instanceof ValueNode) ? (ValueNode<?>) this : null), 
					dataOut);
		}
		else {
			nextWriter = binding().writerFor(this, dataOut);
		}

		logger.trace("[" + this + "] next writer is a [" + nextWriter + "].");
		
		return nextWriter;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + 
				(name == null ? "(unnamed)" : name);
	}
}
