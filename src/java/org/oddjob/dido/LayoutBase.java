package org.oddjob.dido;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.dido.bio.DataBindingIn;
import org.oddjob.dido.io.DataReader;
import org.oddjob.dido.layout.ChildReader;


abstract public class LayoutBase<T> implements Layout, ValueNode<T> {

	private DataBindingIn bin;
	
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
	public void bind(DataBindingIn bin) {
		this.bin = bin;
	}
	
	protected DataBindingIn binding() {
		return bin;
	}
	
	protected DataReader downOurOutReader(final DataInProvider din) {
		
		if (binding() == null) {
			return new ChildReader(childLayouts(), din);
		}
		else {
			return new DataReader() {
				@Override
				public Object read() throws DataException {
					return bin.process(LayoutBase.this, din);
				}
			};
		}
	}
}
