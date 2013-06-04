package org.oddjob.dido;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;
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

	private static final Logger logger = Logger.getLogger(DataReadJob.class);
	
	/** 
	 * @oddjob.property
	 * @oddjob.description The name of this job. 
	 * @oddjob.required No.
	 */
	private String name;
	
    /**
     * @oddjob.property
     * @oddjob.description The plan for reading the data. This is the
     * root node of a structure of data nodes.
     * @oddjob.required Yes.
     */	
	private Layout plan;
	
    /**
     * @oddjob.property
     * @oddjob.description The beans read in.
     * @oddjob.required R/O.
     */	
	private Collection<Object> beans;
	
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
	private Map<String, Binding> bindings = new HashMap<String, Binding>();
	
	@Override
	public void run() {
		if (plan == null) {
			throw new NullPointerException("No Definition.");
		}
		if (input == null) {
			throw new NullPointerException("No Input.");
		}
		if (beans == null) {
			logger.info("No destination for beans!");
		}
		
		logger.info("Starting to read data using [" + plan + "]");
		
		StreamIn in = new InputStreamIn(input);
		
		Layout root = plan;
		root.reset();
		
		BindingHelper bindingHelper = new BindingHelper(root);
		for (Map.Entry<String, Binding> entry: bindings.entrySet()) {
			Binding binding = entry.getValue();
			binding.free();
			bindingHelper.bind(entry.getKey(), binding);
		}

		try {
			DataReader reader = root.readerFor(in);
			
			while (true) {				
				Object bean = reader.read();
				if (bean== null) {
					break;
				}
				if (beans != null) {
					beans.add(bean);
				}
			}
			
			reader.close();
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (DataException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	public Collection<Object> getBeans() {
		return beans;
	}

	public void setBeans(Collection<Object> beans) {
		this.beans = beans;
	}

	public Layout getPlan() {
		return plan;
	}


	public void setPlan(Layout definition) {
		this.plan = definition;
	}


	public InputStream getInput() {
		return input;
	}


	public void setInput(InputStream input) {
		this.input = input;
	}
	
	public void setBindings(String name, Binding binding) {
		if (binding == null) {
			bindings.remove(name);
		}
		else {
			bindings.put(name, binding);
		}
	}
	
	public Binding getBindings(String name) {
		return bindings.get(name);
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		if (name == null) {
			return getClass().getSimpleName();
		}
		return name;
	}	
}
