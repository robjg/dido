package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.bio.Binding;


abstract public class LayoutNode implements Layout {

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
	
	protected DataReader nextReaderFor(final DataIn dataIn) {
		
		if (binding() == null) {
			return new ChildReader(childLayouts(), dataIn);
		}
		else {
			return new DataReader() {
				
				boolean revisit = false;
				
				@Override
				public Object read() throws DataException {
					try {
						return binding.process(LayoutNode.this, dataIn, revisit);
					}
					finally {
						revisit = true;
					}
				}
			};
		}
	}
	
	protected DataWriter nextWriterFor(final DataOut dataOut) {
		
		if (binding() == null) {
			return new ChildWriter(childLayouts(), 
					((this instanceof ValueNode) ? (ValueNode<?>) this : null), 
					dataOut);
		}
		else {
			return new DataWriter() {
				@Override
				public boolean write(Object value) throws DataException {
					return binding.process(value, LayoutNode.this, dataOut);
				}
			};
		}
	}
}
