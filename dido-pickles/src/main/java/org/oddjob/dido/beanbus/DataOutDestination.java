package org.oddjob.dido.beanbus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;
import org.oddjob.dido.layout.LayoutsByName;

/**
 * A Beanbus destination that wraps a {@link DataWriter}.
 * @author rob
 *
 */
public class DataOutDestination implements Runnable, AutoCloseable, Consumer<Object> {

	private static final Logger logger = Logger.getLogger(
			DataOutDestination.class);
	
	private String name;
	
	private Layout layout;
	
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
	
	private DataWriter writer;

	BindingHelper bindingHelper = new BindingHelper();

	@Override
	public void run() {

		if (layout == null) {
			throw new NullPointerException("No Layout.");
		}

		if (data == null) {
			throw new NullPointerException("No output provided");
		}

		layout.reset();

		LayoutsByName layoutsByName = new LayoutsByName(layout);

		bindingHelper = new BindingHelper();

		for (Map.Entry<String, Binding> entry: bindings.entrySet()) {

			Layout layout = layoutsByName.getLayout(entry.getKey());
			if (layout == null) {
				logger.warn("No Layout to bind to named " + name);
			}
			else {
				bindingHelper.bind(layout, entry.getValue());
			}
		}

		try {
			writer = layout.writerFor(data);
		}
		catch (DataException e) {
			throw new IllegalArgumentException("Failed creating writer.", e);
		}

		count = 0;

	}

	@Override
	public void close() throws Exception {

		if (writer != null) {
			try {
				writer.close();
			} catch (DataException e) {
				logger.error("Failed closing output data.", e);
			}

			bindingHelper.freeAll();
		}
	}

	@Override
	public void accept(Object bean) {
		
		try {
			writer.write(bean);
			
			++count;
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

	public DataOut getData() {
		return data;
	}
	
	public void setData(DataOut data) {
		this.data = data;
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
