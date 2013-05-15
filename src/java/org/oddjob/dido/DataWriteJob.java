package org.oddjob.dido;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.dido.bio.BindingOut;
import org.oddjob.dido.io.ConfigurationType;
import org.oddjob.dido.io.DataWriterImpl;
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
	private DataPlan<?, ?, StreamOut, ?> plan;
	
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
	private List<BindingOut> bindings = new ArrayList<BindingOut>();
	
    /**
     * @oddjob.property
     * @oddjob.description How to configure nodes.
     * @oddjob.required No.
     */	
	private ConfigurationType configurationType;
	
	private int beanCount;
	
	@Override
	public void run() {
		beanCount = 0;
		
		if (plan == null) {
			throw new NullPointerException("No Definition.");
		}
		if (output == null) {
			throw new NullPointerException("No Output.");
		}

		StreamOut out = new OutputStreamOut(output);
		
		DataWriterImpl<StreamOut> writer = new DataWriterImpl<StreamOut>(plan, out,
				configurationType);
		
		for (BindingOut binding : bindings) {
			binding.bindTo(plan.getTopNode(), writer);
		}
		
		try {
			for (Object bean : beans) {
				
				writer.write(bean);
				
				++beanCount;
			}
			writer.complete();
			
			output.close();
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


	public DataPlan<?, ?, StreamOut, ?> getPlan() {
		return plan;
	}


	public void setPlan(DataPlan<?, ?, StreamOut, ?> definition) {
		this.plan = definition;
	}


	public OutputStream getOutput() {
		return output;
	}


	public void setOutput(OutputStream output) {
		this.output = output;
	}
	
	public void setBindings(int index, BindingOut binding) {
		new ListSetterHelper<BindingOut>(bindings).set(index, binding);		
	}
	
	public BindingOut getBindings(int index) {
		return bindings.get(index);
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

	public ConfigurationType getConfigurationType() {
		return configurationType;
	}

	public void setConfigurationType(ConfigurationType configurationStrategy) {
		this.configurationType = configurationStrategy;
	}

	public int getBeanCount() {
		return beanCount;
	}
}
