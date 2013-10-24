package org.oddjob.dido;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.BeanDirectoryOwner;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;
import org.oddjob.dido.layout.LayoutDirectoryFactory;
import org.oddjob.dido.layout.LayoutsByName;
import org.oddjob.framework.HardReset;
import org.oddjob.framework.SoftReset;

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
public class DataWriteJob 
implements Runnable, ArooaSessionAware, BeanDirectoryOwner {
	
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
	private int count;
	
	/** Creates the bean directory from the layouts. */
	private LayoutDirectoryFactory layoutDirectoryFactory;
	
	/** Set when running from the layout names. */
	private BeanDirectory beanDirectory;
	
	@ArooaHidden
	@Override
	public void setArooaSession(ArooaSession session) {
		ArooaTools tools = session.getTools();
		this.layoutDirectoryFactory = new LayoutDirectoryFactory(
				tools.getPropertyAccessor(), tools.getArooaConverter());
	}
	
	@HardReset
	@SoftReset
	public void reset() {
		count = 0;
		
		if (layout != null) {
			layout.reset();
		}
	}
	
	@Override
	public void run() {
		count = 0;
		
		if (layout == null) {
			throw new NullPointerException("No Layout provided.");
		}
		
		if (data == null) {
			throw new NullPointerException("No output provided");
		}
		
		if (beans == null) {
			throw new NullPointerException("No beans provided");
		}

		logger.info("Starting to write data using [" + layout + "]");
				
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
			logger.debug("No session set so no bean directory available.");
		}
		else {
			beanDirectory = layoutDirectoryFactory.createFrom(
					layoutsByName.getAll());
		}
		
		try {
			DataWriter writer = layout.writerFor(data);
			
			for (Object bean : beans) {
				
				writer.write(bean);
				
				++count;
			}
			
			writer.close();			
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			logger.info("Wrote " + count + " beans.");
			
			bindingHelper.freeAll();
		}
	}	
	
	@Override
	public BeanDirectory provideBeanDirectory() {
		return beanDirectory;
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

	public int getCount() {
		return count;
	}
}
