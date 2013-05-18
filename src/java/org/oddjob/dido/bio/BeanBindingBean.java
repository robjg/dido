package org.oddjob.dido.bio;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.beanutils.MagicBeanClassCreator;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.BeanView;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Headed;
import org.oddjob.dido.Layout;
import org.oddjob.dido.MorphicnessFactory;
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
import org.oddjob.dido.layout.ChildWriter;
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
	
	private BeanView beanView;
	
	/**
	 * The current bean.
	 */
	private Object bean;
	
	private final List<Runnable> resets = new ArrayList<Runnable>();
	
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
			((ClassMorphic) bindTo).beFor(
					new MorphicnessFactory(accessor).readMorphicnessFor(
							type, beanView));
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
			((ClassMorphic) bindTo).beFor(
					new MorphicnessFactory(accessor).writeMorphicnessFor(
							type, beanView));
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
		public void reset() {
			node.bind(null);
		}
	}
	
	private interface BindingLayoutProcessor {
		public void process(Layout node);
	}

	private class HeaderProcessorIn implements BindingLayoutProcessor {
		
		@Override
		public void process(Layout node) {
			
			final BeanOverview overview = 
					type.getBeanOverview(accessor);
			
			new LayoutWalker() {
				
				@Override
				protected boolean onLayout(final Layout layout) {
					
					final String nodeName = layout.getName();
					
					if (nodeName != null && 
							overview.hasReadableProperty(nodeName)) {
						
						layout.bind(new ChildNodeBinding(layout));
					
						resets.add(new Runnable() {
							@Override
							public void run() {
								layout.bind(null);
							}
						});
						
						return false;
					}
					else {

						return true;
					}
				}
			}.walk(node);
			
			processor = new MainProcessorIn();
			processor.process(node);
		}
	}
	
	private class MainProcessorIn implements BindingLayoutProcessor {
		
		@Override
		public void process(Layout node) {
			
			bean = type.newInstance();
		}
	}
	
	
	private BindingLayoutProcessor processor;
		
	@Override
	public Object process(Layout node, DataIn dataIn,
			boolean revisit) throws DataException {
		
		if (revisit) {
			return null;
		}
		
		if (processor == null) {
			
			if (type == null) {
				
				derriveTypeFromLayout(node);
				
			}
			
			if (node instanceof ClassMorphic) {
				Runnable reset = ((ClassMorphic) node).beFor(
						new MorphicnessFactory(accessor).writeMorphicnessFor(
								type, beanView));
				resets.add(reset);
			}
			
			processor = new HeaderProcessorIn();
		}
		
		processor.process(node);
	
		new ChildReader(node.childLayouts(), dataIn).read();
	
		return bean;
	}
	
	@Override
	public void reset() {
		processor = null;
		for (Runnable reset : resets) {
			reset.run();
		}
		resets.clear();
	}
	
	private class HeaderProcessorOut implements BindingLayoutProcessor {
			
		@Override
		public void process(Layout node) {
			
			final BeanOverview overview = 
					accessor.getClassName(bean).getBeanOverview(accessor);
			
			new LayoutWalker() {
				
				@Override
				protected boolean onLayout(final Layout layout) {
					
					final String nodeName = layout.getName();
					
					if (nodeName != null && 
							overview.hasWriteableProperty(nodeName)) {
						
						layout.bind(new ChildNodeBinding(layout));
					
						resets.add(new Runnable() {
							@Override
							public void run() {
								layout.bind(null);
							}
						});
						
						return false;
					}
					else {

						return true;
					}
				}
			}.walk(node);
			
			processor = new MainProcessorOut();
			processor.process(node);
		}
	}
	
	private class MainProcessorOut implements BindingLayoutProcessor {
		
		@Override
		public void process(Layout node) {
			logger.info("Binding daata to " + node.getName());
		}
	}
	
	@Override
	public boolean process(Object value, Layout node, DataOut dataOut) throws DataException {
		
		if (type != null && !type.forClass().isInstance(value)) {
			return false;
		}
		
		this.bean = value;
		
		if (processor == null) {
			if (type == null) {

				type = accessor.getClassName(value);
				
				resets.add(new Runnable() {
					@Override
					public void run() {
						type = null;
					}
				});
			}
			
			if (node instanceof ClassMorphic) {
				Runnable reset = ((ClassMorphic) node).beFor(
						new MorphicnessFactory(accessor).writeMorphicnessFor(
								type, beanView));
				resets.add(reset);
			}
			
			processor = new HeaderProcessorOut();
		}
		
		processor.process(node);
	
		new ChildWriter(node.childLayouts(), 
				node instanceof ValueNode ? (ValueNode<?>) node : null, 
				dataOut).write(value);
		
		return false;
	}
		
	private void derriveTypeFromLayout(Layout layout) throws DataException {
		
		final MagicBeanClassCreator classCreator = 
				new MagicBeanClassCreator("BeanBinding");

		List<Layout> children = layout.childLayouts();
		
		if (children.size() > 0) {
			
			new LayoutWalker() {				
				@Override
				protected boolean onLayout(Layout layout) {
					if (layout.childLayouts().size() == 0 && 
							layout.getName() != null &&
							layout instanceof ValueNode) {
						
						classCreator.addProperty(layout.getName(), 
								((ValueNode<?>) layout).getType());
					}
					
					return true;
				}
			}.walk(layout);
			
		}
		else if (layout instanceof Headed) {
			
			String[] headings = ((Headed) layout).getHeadings();
			
			if (headings == null) {
				throw new DataException("No headings to create type");
			}
			
			for (String heading: headings) {

				classCreator.addProperty(heading, String.class);
			}			
		}
		else {
				throw new DataException("No type provided and it is not derivable.");
		}
		
		type = classCreator.create();
		
		resets.add(new Runnable() {
			@Override
			public void run() {
				type = null;
			}
		});
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

	public BeanView getBeanView() {
		return beanView;
	}

	public void setBeanView(BeanView beanView) {
		this.beanView = beanView;
	}	
}
