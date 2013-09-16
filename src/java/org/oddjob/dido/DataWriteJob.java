package org.oddjob.dido;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;

/**
 * @oddjob.description A Job that can write data out to a file or 
 * other output.  
 * 
 * @oddjob.exapmle
 * 
 * Writing a delimited file.
 * 
 * {@oddjob.xml.resource org/oddjob/dido/WriteJobExample.xml}
 * 
 * The plan is 
 * 
 * {@oddjob.xml.resource org/oddjob/dido/DelimitedExamplePlan.xml}
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
     * @oddjob.description The layout of the data. This is the
     * root node of a hierarchy of layout nodes.
     * @oddjob.required Yes.
     */	
	private Layout layout;
	
    /**
     * @oddjob.property
     * @oddjob.description The beans to write out.
     * @oddjob.required Yes.
     */	
	private Iterable<Object> beans;
	
    /**
     * @oddjob.property
     * @oddjob.description The Data Out to write the data to.
     * @oddjob.required Yes.
     */	
	private DataOut data;
	
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
		
		if (layout == null) {
			throw new NullPointerException("No Layout provided.");
		}
		
		if (data == null) {
			throw new NullPointerException("No output provided");
		}

		logger.info("Starting to write data using [" + layout + "]");
				
		Layout root = layout;
		root.reset();
		
		BindingHelper bindingHelper = new BindingHelper(root);
		for (Map.Entry<String, Binding> entry: bindings.entrySet()) {
			Binding binding = entry.getValue();
			bindingHelper.bind(entry.getKey(), binding);
		}
		
		try {
			DataWriter writer = root.writerFor(data);
			
			for (Object bean : beans) {
				
				writer.write(bean);
				
				++beanCount;
			}
			
			writer.close();
			
			if (data instanceof Closeable) {
				((Closeable) data).close();
			}
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			for (Map.Entry<String, Binding> entry: bindings.entrySet()) {
				Binding binding = entry.getValue();
				binding.free();
				bindingHelper.bind(entry.getKey(), null);
			}
		}

		logger.info("Wrote " + beanCount + " beans of data out.");
	}	
	
	public void setBeans(Iterable<Object> beans) {
		this.beans = beans;
	}


	public Layout getLayout() {
		return layout;
	}


	public void setLayout(Layout definition) {
		this.layout = definition;
	}


	public DataOut getData() {
		return data;
	}


	public void setData(DataOut output) {
		this.data = output;
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
