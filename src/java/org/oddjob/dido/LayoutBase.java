package org.oddjob.dido;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.bio.DataBinding;
import org.oddjob.dido.layout.ChildReader;
import org.oddjob.dido.layout.ChildWriter;


abstract public class LayoutBase<T> implements Layout, ValueNode<T> {

	private DataBinding bin;
	
	private String name;
	
	/** The stencils value. */
	private T value;

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
	public T value() {
		return value;
	}
	
	@Override
	public void value(T value) {
		this.value = value;
	}
	
	@Override
	public List<Layout> childLayouts() {
		return children;
	}
	
	@Override
	public void bind(DataBinding bin) {
		this.bin = bin;
	}
	
	protected DataBinding binding() {
		return bin;
	}
	
	protected DataReader downOurOutReader(final DataInProvider dataIn) {
		
		if (binding() == null) {
			return new ChildReader(childLayouts(), dataIn);
		}
		else {
			return new DataReader() {
				
				boolean revisit = false;
				
				@Override
				public Object read() throws DataException {
					try {
						return bin.process(LayoutBase.this, dataIn, revisit);
					}
					finally {
						revisit = true;
					}
				}
			};
		}
	}
	
	protected DataWriter downOrOutWriter(final DataOutProvider dataOut) {
		
		if (binding() == null) {
			return new ChildWriter(childLayouts(), dataOut);
		}
		else {
			return new DataWriter() {
				@Override
				public boolean write(Object value) throws DataException {
					return bin.process(value, LayoutBase.this, dataOut);
				}
			};
		}
	}
}
