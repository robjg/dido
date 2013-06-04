package org.oddjob.dido;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;
import org.oddjob.dido.stream.OutputStreamOut;
import org.oddjob.dido.stream.StreamOut;

/**
 * @oddjob.description A Job that can write data out to a file or 
 * other output.  
 * 
 * @author rob
 *
 */
public class DataWriteJob implements Runnable {
	private static final Logger logger = Logger.getLogger(DataWriteJob.class);
	
    /**
     * @oddjob.property
     * @oddjob.description The job's name.
     * @oddjob.required No.
     */	
	private String name;
	
    /**
     * @oddjob.property
     * @oddjob.description Guide for writing the data. This is the
     * root node of a structure of data nodes.
     * @oddjob.required Yes.
     */	
	private Layout plan;
	
    /**
     * @oddjob.property
     * @oddjob.description The beans to write out.
     * @oddjob.required Yes.
     */	
	private Iterable<Object> beans;
	
    /**
     * @oddjob.property
     * @oddjob.description The output to write the data to.
     * @oddjob.required Yes.
     */	
	private OutputStream output;
	
    /**
     * @oddjob.property
     * @oddjob.description Bindings between beans and data out.
     * @oddjob.required Yes.
     */	
	private Map<String, Binding> bindings = new HashMap<String, Binding>();
		
	/**
     * @oddjob.property
     * @oddjob.description The number of beans written.
     * @oddjob.required Read Only.
	 */
	private int beanCount;
	
	@Override
	public void run() {
		beanCount = 0;
		
		if (plan == null) {
			throw new NullPointerException("No Definition.");
		}

		logger.info("Starting to write data using [" + plan + "]");
		
		OutputStream output = this.output;
		
		StreamOut dataOut = null;
		if (output != null) {
			dataOut = new OutputStreamOut(output);
		}
		
		Layout root = plan;
		root.reset();
		
		BindingHelper bindingHelper = new BindingHelper(root);
		for (Map.Entry<String, Binding> entry: bindings.entrySet()) {
			Binding binding = entry.getValue();
			binding.free();
			bindingHelper.bind(entry.getKey(), binding);
		}
		
		try {
			DataWriter writer = root.writerFor(dataOut);
			
			for (Object bean : beans) {
				
				writer.write(bean);
				
				++beanCount;
			}
			
			writer.close();
			
			if (output != null) {
				output.close();
			}
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		logger.info("Wrote " + beanCount + " beans of data out.");
	}	
	
	public void setBeans(Iterable<Object> beans) {
		this.beans = beans;
	}


	public Layout getPlan() {
		return plan;
	}


	public void setPlan(Layout definition) {
		this.plan = definition;
	}


	public OutputStream getOutput() {
		return output;
	}


	public void setOutput(OutputStream output) {
		this.output = output;
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
	
	public String toString() {
		if (name != null) {
			return name;
		}
		else {
			return getClass().getSimpleName();
		}
	}

	public int getBeanCount() {
		return beanCount;
	}
}
