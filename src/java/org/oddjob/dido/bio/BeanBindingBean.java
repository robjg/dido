package org.oddjob.dido.bio;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.SupportsChildren;
import org.oddjob.dido.io.ClassMorphic;
import org.oddjob.dido.io.DataLinkIn;
import org.oddjob.dido.io.DataLinkOut;
import org.oddjob.dido.io.LinkInControl;
import org.oddjob.dido.io.LinkInEvent;
import org.oddjob.dido.io.LinkOutEvent;
import org.oddjob.dido.io.LinkableIn;
import org.oddjob.dido.io.LinkableOut;
import org.oddjob.dido.io.Nodes;

/**
 * @oddjob.description Provide a binding to bean of the given type. 
 * <p>
 * At the moment property names must match node names, but this will change 
 * soon.
 * 
 * @author rob
 *
 */
public class BeanBindingBean 
implements BindingIn, BindingOut, ArooaSessionAware {

	private static final Logger logger = Logger.getLogger(BeanBindingBean.class);
	
	/**
	 * PropertyAccessor with conversions.
	 */
	private PropertyAccessor accessor;
	
    /**
     * @oddjob.property 
     * @oddjob.description The name of the node we're binding to.
     * @oddjob.required Yes.
     */
	private String node;
	
    /**
     * @oddjob.property
     * @oddjob.description The bean class type.
     * @oddjob.required Yes.
     */
	private Class<?> type;
	
	/**
	 * The current bean.
	 */
	private Object bean;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.accessor = session.getTools().getPropertyAccessor(
				).accessorWithConversions(
						session.getTools().getArooaConverter());
	}
	
	@Override
	public void bindTo(DataNode<?, ?, ?, ?> root, LinkableIn linkable) {
	
		if (node == null) {
			throw new NullPointerException("No Node.");
		}
		
		Nodes nodes = new Nodes(root);
	
		DataNode<?, ?, ?, ?> bindTo = nodes.getNode(node);
		
		if (bindTo == null) {
			throw new IllegalArgumentException("Failed to find " + node);
		}

		logger.debug("Binding " + node + " to instances of " + type.getName());
		linkable.setControlIn(bindTo, new BeanFactory());
		
		if (bindTo instanceof ClassMorphic) {
			((ClassMorphic) bindTo).beFor(new SimpleArooaClass(type));
		}
		
		new DispatchBuilder().build(bindTo, linkable);
	}
	
	class BeanFactory implements DataLinkIn {
		
		@Override
		public LinkInControl dataIn(LinkInEvent event) {
			try {
				bean = type.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			
			return new LinkInControl() {
				
				@Override
				public Object getDataObject() {
					return bean;
				}
			};
		}

		@Override
		public void lastIn(LinkInEvent event) {
		}
	}
	
	class DispatchBuilder {
		BeanOverview overview = accessor.getBeanOverview(type);
		
		void build(DataNode<?, ?, ?, ?> node, LinkableIn link) {
			
			String name = node.getName();
			if (node instanceof ValueNode && 
					name != null && 
					overview.hasWriteableProperty(name)) {
				link.setControlIn(node, new PropertyDispatch(name));
				logger.debug("Linking property to node " + name);
			}
			
			if (node instanceof SupportsChildren) {
				DataNode<?, ?, ?, ?>[] nodes = 
					((SupportsChildren) node).childrenToArray();

				for (DataNode<?, ?, ?, ?> child : nodes) {
					build(child, link);
				}
			}
		}
	}	
	
	class PropertyDispatch implements DataLinkIn {
		
		private final PropertySetter setter;

		public PropertyDispatch(String property) {
			setter = new PropertySetter(property);
		}

		@Override
		public LinkInControl dataIn(LinkInEvent event) {
			
			DataNode<?, ?, ?, ?> node = event.getNode();
			
			setter.setValue(((ValueNode<?>) node).value());
			
			return null;
		}

		@Override
		public void lastIn(LinkInEvent event) {
			// Nothing to do.
		}
	}
	
	class PropertySetter {
		
		private final String property;
		
		PropertySetter(String property){
			this.property = property;
		}
		
		void setValue(Object value) {
			accessor.setProperty(bean, property, value);
		}
	}
		
	@Override
	public void bindTo(DataNode<?, ?, ?, ?> root, LinkableOut linkable) {
	
		if (node == null) {
			throw new NullPointerException("No Node.");
		}
		
		Nodes nodes = new Nodes(root);
	
		DataNode<?, ?, ?, ?> bindTo = nodes.getNode(node);
		
		if (bindTo == null) {
			throw new IllegalArgumentException("Failed to find " + node);
		}
		
		logger.debug("Binding " + node + " to instances of " + type.getName());
		linkable.setLinkOut(bindTo, new BeanAcceptor());
		
		if (bindTo instanceof ClassMorphic) {
			((ClassMorphic) bindTo).beFor(new SimpleArooaClass(type));
		}
		
		PropertiesToNodes propertyBinding = 
			new PropertiesToNodes(new SimpleArooaClass(type), 
					accessor);
		
		propertyBinding.bindTo(bindTo, linkable);
		
	}
	
	class BeanAcceptor implements DataLinkOut {
		
		@Override
		public boolean dataOut(LinkOutEvent event, Object bean) {
			if (type.isInstance(bean)) {
				BeanBindingBean.this.bean = bean;
				return true;
			}
			else {
				return false;
			}
		}		
		
		@Override
		public void lastOut(LinkOutEvent event) {
			// Nothing to do.
		}
	}	
	
	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}	
}
