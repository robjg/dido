package org.oddjob.dido.bio;

import org.apache.log4j.Logger;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.Stencil;
import org.oddjob.dido.SupportsChildren;
import org.oddjob.dido.io.DataLinkOut;
import org.oddjob.dido.io.LinkOutEvent;
import org.oddjob.dido.io.LinkableOut;

public class PropertiesToNodes implements BindingOut {
	private static final Logger logger = Logger.getLogger(PropertiesToNodes.class);
	
	private final ArooaClass arooaClass;
	
	private final PropertyAccessor accessor;
	
	public PropertiesToNodes(ArooaClass arooaClass, PropertyAccessor accessor) {
		if (arooaClass == null) {
			throw new NullPointerException("No ArooaClass.");
		}
		if (accessor == null) {
			throw new NullPointerException("No PropertyAccessor.");
		}
		
		this.arooaClass = arooaClass;
		this.accessor = accessor;		
	}
	
	@Override
	public void bindTo(DataNode<?, ?, ?, ?> node, LinkableOut linkable) {
		BeanOverview overview = arooaClass.getBeanOverview(accessor);		
		
		build(node, linkable, overview);
	}
	
	private void build(DataNode<?, ?, ?, ?> node, LinkableOut link, BeanOverview overview) {

		String name = node.getName();
		if (node instanceof Stencil && 
				name != null && 
				overview.hasReadableProperty(name)) {
			link.setLinkOut(node, new PropertyRetrieval(name));
			logger.debug("Linking property to node " + name);
		}

		if (node instanceof SupportsChildren) {
			DataNode<?, ?, ?, ?>[] nodes = 
				((SupportsChildren) node).childrenToArray();

			for (DataNode<?, ?, ?, ?> child : nodes) {
				build(child, link, overview);
			}
		}

	}

	
	class PropertyRetrieval implements DataLinkOut {
		
		private final PropertyGetter getter;
		
		PropertyRetrieval(String property) {
			getter = new PropertyGetter(property);
		}
				
		@Override
		public boolean dataOut(LinkOutEvent event, Object bean) {
			DataNode<?, ?, ?, ?> node = event.getNode();

			setStencilValue((Stencil<?>) node, bean);
			
			return true;
		}
		
		@Override
		public void lastOut(LinkOutEvent event) {
			// Nothing to do.
		}
		
		private <T> void setStencilValue(Stencil<T> stencil, Object bean) {
			try {
				T value = getter.getValue(bean, stencil.getType());				
				stencil.value(value);
			} catch (ArooaPropertyException e) {
				throw new RuntimeException(e);
			} catch (ArooaConversionException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	class PropertyGetter {
		
		private final String property;
		
		PropertyGetter(String property){
			this.property = property;
		}
		
		<T> T getValue(Object bean, Class<T> type) throws ArooaPropertyException, ArooaConversionException {
			return accessor.getProperty(bean, property, type);
		}
	}
	
	
}
