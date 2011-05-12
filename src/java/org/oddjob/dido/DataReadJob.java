package org.oddjob.dido;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.dido.bio.BindingIn;
import org.oddjob.dido.io.DataReader;
import org.oddjob.dido.stream.InputStreamIn;
import org.oddjob.dido.stream.StreamIn;

/**
 * @oddjob.description A Job that can read data from a file or 
 * some other input.  
 * 
 * @oddjob.example
 * 
 * Reading a delimited file.
 * 
 * {@oddjob.xml.resource org/oddjob/dido/ReadJobExample.xml}
 * 
 * The plan is 
 * 
 * {@oddjob.xml.resource org/oddjob/dido/DelimitedExamplePlan.xml}
 * 
 * @author rob
 *
 */
public class DataReadJob implements Runnable {

    /**
     * @oddjob.property
     * @oddjob.description The plan for reading the data. This is the
     * root node of a structure of data nodes.
     * @oddjob.required Yes.
     */	
	private DataPlan<StreamIn, ?, ?, ?> plan;
	
    /**
     * @oddjob.property
     * @oddjob.description The beans read in.
     * @oddjob.required R/O.
     */	
	private Object[] beans;
	
    /**
     * @oddjob.property
     * @oddjob.description The input to read the data from.
     * @oddjob.required Yes.
     */	
	private InputStream input;
	
    /**
     * @oddjob.property
     * @oddjob.description Bindings between beans and data in.
     * @oddjob.required Yes.
     */	
	private List<BindingIn> bindings = new ArrayList<BindingIn>();
	
	@Override
	public void run() {
		if (plan == null) {
			throw new NullPointerException("No Definition.");
		}
		if (input == null) {
			throw new NullPointerException("No Input.");
		}

		StreamIn in = new InputStreamIn(input);
		
		DataReader<StreamIn> reader = new DataReader<StreamIn>(plan, in);
		
		for (BindingIn binding : bindings) {
			binding.bindTo(plan.getTopNode(), reader);
		}
		
		List<Object> beans = new ArrayList<Object>();
		
		try {
			while (true) {				
				Object bean = reader.read();
				if (bean== null) {
					break;
				}
				beans.add(bean);
			}
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		this.beans = beans.toArray();
	}
	
	
	public Object[] getBeans() {
		return beans;
	}


	public DataPlan<StreamIn, ?, ?, ?> getPlan() {
		return plan;
	}


	public void setPlan(DataPlan<StreamIn, ?, ?, ?> definition) {
		this.plan = definition;
	}


	public InputStream getInput() {
		return input;
	}


	public void setInput(InputStream input) {
		this.input = input;
	}
	
	public void setBindings(int index, BindingIn binding) {
		new ListSetterHelper<BindingIn>(bindings).set(index, binding);		
	}
	
	public BindingIn getBindings(int index) {
		return bindings.get(index);
	}
}
