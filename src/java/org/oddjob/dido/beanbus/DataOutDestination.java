package org.oddjob.dido.beanbus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.beanbus.AbstractDestination;
import org.oddjob.beanbus.BusConductor;
import org.oddjob.beanbus.BusCrashException;
import org.oddjob.beanbus.TrackingBusListener;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;
import org.oddjob.dido.stream.OutputStreamOut;

public class DataOutDestination extends AbstractDestination<Object>{
	private static final Logger logger = Logger.getLogger(
			DataOutDestinationTest.class);
	
	private String name;
	
	private Layout layout;
	
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
	private int count;
	
	private DataWriter writer;
	
	private final TrackingBusListener busListener = new TrackingBusListener() {
		
		@Override
		public void busStarting(org.oddjob.beanbus.BusEvent event) 
		throws BusCrashException {
			
			if (layout == null) {
				throw new BusCrashException("No Layout.");
			}
			
			DataOut dataOut = null;
			if (output != null) {
				dataOut = new OutputStreamOut(output);
			}
			
			BindingHelper bindingHelper = new BindingHelper(layout);
			for (Map.Entry<String, Binding> entry: bindings.entrySet()) {
				Binding binding = entry.getValue();
				binding.reset();
				bindingHelper.bind(entry.getKey(), binding);
			}
			
			try {
				writer = layout.writerFor(dataOut);
			} catch (DataException e) {
				throw new BusCrashException("Failed creating writer.", e);
			}
			
			count = 0;
		}

		@Override
		public void busTerminated(org.oddjob.beanbus.BusEvent event) {
			
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.error("Failed closing output.", e);
				}
			}
		}
	};
		
	@Inject
	@ArooaHidden
	public void setBusConductor(BusConductor busConductor) {
		busListener.setBusConductor(busConductor);
	}
	
	@Override
	public boolean add(Object bean) {
		
		try {
			writer.write(bean);
			
			++count;
			
			return true;
		}
		catch (DataException exception){
			throw new IllegalArgumentException("Failed writing bean.");
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		if (name == null) {
			return getClass().getSimpleName();
		}
		else {
			return name;
		}
	}

}
