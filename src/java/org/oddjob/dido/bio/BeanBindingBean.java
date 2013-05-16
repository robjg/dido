package org.oddjob.dido.bio;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;
import org.oddjob.dido.SupportsChildren;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.io.ClassMorphic;
import org.oddjob.dido.io.DataLinkIn;
import org.oddjob.dido.io.DataLinkOut;
import org.oddjob.dido.io.LinkInControl;
import org.oddjob.dido.io.LinkInEvent;
import org.oddjob.dido.io.LinkOutEvent;
import org.oddjob.dido.io.LinkableIn;
import org.oddjob.dido.io.LinkableOut;
import org.oddjob.dido.io.Nodes;
import org.oddjob.dido.layout.ChildReader;
import org.oddjob.dido.layout.LayoutWalker;

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
implements BindingIn, BindingOut, Binding, ArooaSessionAware {

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
	private ArooaClass type;
	
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

		logger.debug("Binding " + node + " to instances of " + type.toString());
		linkable.setControlIn(bindTo, new BeanFactory());
		
		if (bindTo instanceof ClassMorphic) {
			((ClassMorphic) bindTo).beFor(type);
		}
		
		new DispatchBuilder().build(bindTo, linkable);
	}
	
	class BeanFactory implements DataLinkIn {
		
		@Override
		public LinkInControl dataIn(LinkInEvent event) {
			
			bean = type.newInstance();
			
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
		BeanOverview overview = type.getBeanOverview(accessor);
		
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
		
		logger.debug("Binding " + node + " to instances of " + type.toString());
		linkable.setLinkOut(bindTo, new BeanAcceptor());
		
		if (bindTo instanceof ClassMorphic) {
			((ClassMorphic) bindTo).beFor(type);
		}
		
		PropertiesToNodes propertyBinding = 
			new PropertiesToNodes(type, accessor);
		
		propertyBinding.bindTo(bindTo, linkable);
		
	}
	
	class BeanAcceptor implements DataLinkOut {
		
		@Override
		public boolean dataOut(LinkOutEvent event, Object bean) {
				BeanBindingBean.this.bean = bean;
				return true;
		}		
		
		@Override
		public void lastOut(LinkOutEvent event) {
			// Nothing to do.
		}
	}	

	private class ChildNodeBinding implements Binding {

		private final Layout node;
		
		public ChildNodeBinding(Layout node) {
			this.node = node;
		}
		
		
		@Override
		public Object process(Layout node, DataIn dataIn, 
				boolean revisit) {
			
			if (revisit) {
				return null;
			}
			
			accessor.setSimpleProperty(bean, node.getName(), 
					((ValueNode<?>) node).value());
			
			return null;
		}
		
		@Override
		public boolean process(Object value, 
				Layout node, DataOut dataOut) 
		throws DataException {
			
			ValueNode<?> valueNode = (ValueNode<?>) node;
			
			processInferType(value, valueNode, dataOut);
			
			return false;
		}
		
		public <T> void processInferType(Object value, 
				ValueNode<T> valueNode, DataOut dataOut) 
		throws DataException {

			Class<T> type = valueNode.getType();
			
			try {
				Object fieldValue = accessor.getProperty(value, 
						node.getName(), type);
				
				valueNode.value(type.cast(fieldValue));
				
			} catch (ArooaPropertyException e) {
				throw new DataException(e);
			} catch (ArooaConversionException e) {
				throw new DataException(e);
			}
			
		}
		
		@Override
		public void close() {
			node.bind(null);
		}
	}
	
	private interface ProcessorIn {
		public void process(Layout node);
	}

	private class HeaderProcessorIn implements ProcessorIn {
		
		private BeanOverview overview;
		
		@Override
		public void process(Layout node) {
			
			new LayoutWalker() {
				
				@Override
				protected boolean onLayout(final Layout layout) {
					
					final String nodeName = layout.getName();
					
					if (nodeName != null && 
							overview.hasReadableProperty(nodeName)) {
						
						layout.bind(new ChildNodeBinding(layout));
					
						return false;
					}
					else {

						return true;
					}
				}
			}.walk(node);
			
			processorIn = new BodyProcessor();
			processorIn.process(node);
		}
	}
	
	private class BodyProcessor implements ProcessorIn {
		
		@Override
		public void process(Layout node) {
			
			bean = type.newInstance();
		}
	}
	
	
	private ProcessorIn processorIn;
		
	@Override
	public Object process(Layout node, DataIn dataIn,
			boolean revisit) throws DataException {
		
		if (revisit) {
			return null;
		}
		
		if (processorIn == null) {
			processorIn = new HeaderProcessorIn();
		}
		
		processorIn.process(node);
	
		new ChildReader(node.childLayouts(), dataIn).read();
	
		return bean;
	}
	
	@Override
	public void close() {
		processorIn = null;
	}
	
	@Override
	public boolean process(Object value, Layout node, DataOut dataOut) {
		
		if (type != null && !type.forClass().isInstance(value)) {
			return false;
		}
		
		return true;
	}
	
	
	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public ArooaClass getType() {
		return type;
	}

	public void setType(ArooaClass type) {
		this.type = type;
	}	
}
