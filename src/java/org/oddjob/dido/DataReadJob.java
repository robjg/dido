package org.oddjob.dido;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;

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
     * @oddjob.description The layout of the data. This is the
     * root node of a structure of layout nodes.
     * @oddjob.required Yes.
     */	
	private Layout layout;
	
    /**
     * @oddjob.property
     * @oddjob.description The beans read in.
     * @oddjob.required R/O.
     */	
	private Collection<Object> beans;
	
    /**
     * @oddjob.property
     * @oddjob.description The data to read.
     * @oddjob.required Yes.
     */	
	private DataIn data;
	
    /**
     * @oddjob.property
     * @oddjob.description Bindings between beans and data in.
     * @oddjob.required No, but pointless if missing.
     */	
	private Map<String, Binding> bindings = new HashMap<String, Binding>();
	
	@Override
	public void run() {
		if (layout == null) {
			throw new NullPointerException("No Layout provided.");
		}
		if (data == null) {
			throw new NullPointerException("No Input provided.");
		}
		if (beans == null) {
			logger.info("No destination for beans!");
		}
		
		logger.info("Starting to read data using [" + layout + "]");
		
		Layout root = layout;
		root.reset();
		
		BindingHelper bindingHelper = new BindingHelper(root);
		for (Map.Entry<String, Binding> entry: bindings.entrySet()) {
			Binding binding = entry.getValue();
			bindingHelper.bind(entry.getKey(), binding);
		}

		try {
			DataReader reader = root.readerFor(data);
			
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
		finally {
			for (Map.Entry<String, Binding> entry: bindings.entrySet()) {
				Binding binding = entry.getValue();
				binding.free();
				bindingHelper.bind(entry.getKey(), null);
			}
		}
		
	}
	
	
	public Collection<Object> getBeans() {
		return beans;
	}

	public void setBeans(Collection<Object> beans) {
		this.beans = beans;
	}

	public Layout getLayout() {
		return layout;
	}


	public void setLayout(Layout definition) {
		this.layout = definition;
	}


	public DataIn getData() {
		return data;
	}


	public void setData(DataIn input) {
		this.data = input;
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
