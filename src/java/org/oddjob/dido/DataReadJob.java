package org.oddjob.dido;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.BeanDirectoryOwner;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;
import org.oddjob.dido.layout.LayoutDirectoryFactory;
import org.oddjob.dido.layout.LayoutsByName;

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
public class DataReadJob 
implements Runnable, ArooaSessionAware, BeanDirectoryOwner  {

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

	/** Creates the bean directory from the layouts. */
	private LayoutDirectoryFactory layoutDirectoryFactory;
	
	/** Set when running from the layout names. */
	private BeanDirectory beanDirectory;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		ArooaTools tools = session.getTools();
		this.layoutDirectoryFactory = new LayoutDirectoryFactory
				(tools.getPropertyAccessor(), tools.getArooaConverter());		
	}
	
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
		
		layout.reset();

		LayoutsByName layoutsByName = new LayoutsByName(layout);
		
		BindingHelper bindingHelper = new BindingHelper();
		
		for (Map.Entry<String, Binding> entry: bindings.entrySet()) {
			
			Layout layout = layoutsByName.getLayout(entry.getKey());
			if (layout == null) {
				logger.warn("No Layout to bind to named " + name);
			}
			else {
				bindingHelper.bind(layout, entry.getValue());
			}
		}
		
		if (layoutDirectoryFactory == null) {
			throw new NullPointerException("Call setArooaSession before run!");
		}
		else {
			beanDirectory = layoutDirectoryFactory.createFrom(
					layoutsByName.getAll());
		}
		
		try {
			DataReader reader = layout.readerFor(data);
			
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
			bindingHelper.freeAll();
		}
		
	}
	
	@Override
	public BeanDirectory provideBeanDirectory() {
		return beanDirectory;
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
