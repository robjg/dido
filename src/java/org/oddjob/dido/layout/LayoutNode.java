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
		
		if (binding() == null) {
			return new ChildReader(childLayouts(), dataIn);
		}
		else {
			logger.trace("[" + this + "] next reader is a Binding.");
			
			return new DataReader() {
				
				boolean revisit = false;
				
				@Override
				public Object read() throws DataException {
					try {
						return binding.extract(LayoutNode.this, dataIn, revisit);
					}
					finally {
						revisit = true;
					}
				}
				
				@Override
				public void close() throws DataException {
					
					// Todo: Binding needs to be closeable.
				}
				
				@Override
				public String toString() {
					
					return "BindingReader for " + LayoutNode.this;
				}
			};
		}
	}
	
	protected DataWriter nextWriterFor(final DataOut dataOut) throws DataException {
		
		if (binding() == null) {
			return new ChildWriter(childLayouts(), 
					((this instanceof ValueNode) ? (ValueNode<?>) this : null), 
					dataOut);
		}
		else {
			logger.trace("[" + this + "] next writer is a Binding.");
			
			return new DataWriter() {
				@Override
				public boolean write(Object value) throws DataException {
					return binding.inject(value, LayoutNode.this, dataOut);
				}
				
				@Override
				public void close() throws DataException {
					
					// Todo: Binding need to be closeable.
				}
				
				public String toString() {
				
					return "BindingWriter for " + LayoutNode.this;
				}
			};
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + 
				(name == null ? "(unnamed)" : name);
	}
}
