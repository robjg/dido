package org.oddjob.dido.bio;

import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.BeanView;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.utils.Iterables;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.layout.ChildReader;
import org.oddjob.dido.layout.ChildWriter;
import org.oddjob.dido.layout.LayoutWalker;
import org.oddjob.dido.morph.MorphDefinition;
import org.oddjob.dido.morph.MorphDefinitionFactory;
import org.oddjob.dido.morph.MorphProvider;
import org.oddjob.dido.morph.Morphable;

/**
 * @oddjob.description Provide a binding to bean of the given type. 
 * <p>
 * For a property of the bean to be bound to a node, the property name 
 * must match node name.
 * 
 * @author rob
 *
 */
public class BeanBindingBean extends SingleBeanBinding
implements Binding, ArooaSessionAware {

	private static final Logger logger = Logger.getLogger(BeanBindingBean.class);
	
	/** PropertyAccessor with conversions. */
	private PropertyAccessor accessor;
	
    /**
     * @oddjob.property
     * @oddjob.description The bean class type.
     * @oddjob.required Yes.
     */
	private ArooaClass type;
	
	/**
     * @oddjob.property
     * @oddjob.description Provide a view of the bean to {@link Morphable}
     * layouts.
     * @oddjob.required No.
	 */
	private BeanView beanView;
	
	/** The current bean. */
	private Object bean;
	
	/** Keep track of additional bindings this binding has made. */
	private final Resets resets = new Resets();
	
	/** The current node processor for either reading or writing. */
	private BindingLayoutProcessor processor;
	
	@ArooaHidden
	@Override
	public void setArooaSession(ArooaSession session) {
		this.accessor = session.getTools().getPropertyAccessor(
				).accessorWithConversions(
						session.getTools().getArooaConverter());
	}
	
	/**
	 * The binding added to child node to get and set the properties
	 * of a bean with values to and from the node.
	 */	
	private class ChildNodeBinding extends SingleBeanBinding
	implements Binding {

		private final Layout node;
		
		public ChildNodeBinding(Layout node) {
			this.node = node;
			logger.debug("BeanBinding bound for property " + node.getName());
		}
		
		
		@Override
		protected Object extract(Layout node, DataIn dataIn) {
			
			accessor.setSimpleProperty(bean, node.getName(), 
					((ValueNode<?>) node).value());
			
			return null;
		}
		
		@Override
		protected void inject(Object value, 
				Layout node, DataOut dataOut) 
		throws DataException {
			
			ValueNode<?> valueNode = (ValueNode<?>) node;
			
			processInferType(value, valueNode);
		}
		
		/**
		 * Set the value of the node with the property using generics
		 * to infer the type.
		 * 
		 * @param value
		 * @param valueNode
		 * 
		 * @throws DataException
		 */
		private <T> void processInferType(Object value, 
				ValueNode<T> valueNode) 
		throws DataException {

			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) valueNode.getType();
			
			try {
				T fieldValue = accessor.getProperty(value, 
						node.getName(), type);
				
				valueNode.value(fieldValue);
				
			} catch (ArooaPropertyException e) {
				throw new DataException(e);
			} catch (ArooaConversionException e) {
				throw new DataException(e);
			}
		}
		
		@Override
		public void free() {
		}
	}
	
	/**
	 * Implementations process the Layout and it's children binding the 
	 * child nodes to properties of the bean.
	 */
	private interface BindingLayoutProcessor {
		public void process(Layout node);
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.dido.bio.SingleBeanBinding#extract(org.oddjob.dido.Layout, org.oddjob.dido.DataIn)
	 */
	@Override
	protected Object extract(Layout node, DataIn dataIn) 
	throws DataException {
		
		if (processor == null) {
			
			if (type == null) {
				
				derriveTypeFromLayout(node);
			}
			
			if (node instanceof Morphable) {
				
				logger.debug("Giving " + node + " the opportunity to morph.");
				
				Runnable reset = ((Morphable) node).morphInto(
						new MorphDefinitionFactory(accessor).writeableMorphMetaDataFor(
								type, beanView));
				resets.add(reset);
			}
			
			processor = new HeaderProcessorIn();
		}
		
		processor.process(node);
	
		new ChildReader(node.childLayouts(), dataIn).read();
	
		return bean;
	}
	
	/**
	 * Utility method to derive the type.
	 *  
	 * @param layout
	 * @throws DataException
	 */
	private void derriveTypeFromLayout(Layout layout) throws DataException {
		
		final BeanDefinitionBuilder beanDefinitionBuilder = 
				new BeanDefinitionBuilder();

		List<Layout> children = Iterables.toList(layout.childLayouts());
		
		if (children.size() > 0) {
			
			logger.debug("Deriving Magic Bean Properties from child layouts.");
			
			new LayoutWalker() {				
				@Override
				protected boolean onLayout(Layout layout) {
					
					List<Layout> children = Iterables.toList(
							layout.childLayouts());
					
					if (children.size() == 0 && 
							layout.getName() != null &&
							layout instanceof ValueNode) {
						
						String propertyName = layout.getName();
						Class<?> propertyType = 
								((ValueNode<?>) layout).getType();
						
						logger.debug("Adding property " + propertyName + 
								" of type " + propertyType.getName());
						
						beanDefinitionBuilder.addProperty(propertyName, 
								propertyType);
					}
					
					return true;
				}
			}.walkChildren(layout);
			
		}
		else if (layout instanceof MorphProvider) {
			
			logger.debug("Deriving Magic Bean Properties from Morph Definition.");
			
			MorphDefinition metaData = ((MorphProvider) layout).morphOf();
			
			if (metaData == null) {
				throw new DataException("No headings to create type");
			}
			
			for (String propertyName: metaData.getNames()) {

				Class<?> propertyType = metaData.typeOf(propertyName);
				
				logger.debug("Adding property " + propertyName + 
						" of type " + propertyType.getName());
				
				beanDefinitionBuilder.addProperty(propertyName, 
						propertyType);
			}			
		}
		else {
			throw new DataException(
					"No type provided for binding and it is not derivable.");
		}
		
		type = beanDefinitionBuilder.createType();
		beanView = beanDefinitionBuilder.createBeanView();
		
		resets.add(new Runnable() {
			@Override
			public void run() {
				type = null;
				beanView = null;
			}
		});
	}
	
	/**
	 * The initial processor that binds to children for reading values
	 * from layout nodes and setting them as properties of the bean.
	 */
	private class HeaderProcessorIn implements BindingLayoutProcessor {
		
		@Override
		public void process(Layout node) {
			
			final BeanOverview overview = 
					type.getBeanOverview(accessor);
			
			new LayoutWalker() {
				
				@Override
				protected boolean onLayout(final Layout layout) {
					
					final String nodeName = layout.getName();
					
					if (nodeName == null) {
						
						logger.debug("Binding ignoring node [" + layout + 
								"] as it has no name.");

						return true;
					}
					
					if (overview.hasWriteableProperty(nodeName)) {
						
						layout.bind(new ChildNodeBinding(layout));
					
						logger.debug("Binding to child [" + layout + 
								"] on writeable property [" + nodeName + "]");
						
						resets.add(new Runnable() {
							@Override
							public void run() {
								layout.bind(null);
							}
						});
						
						return false;
					}
					else {

						logger.debug("Binding ignoring [" + layout + 
								"] as there is no simple writeable property [" +
								nodeName + "]");
						
						return true;
					}
				}
			}.walkChildren(node);
			
			processor = new MainProcessorIn();
			processor.process(node);
		}
	}
	
	/**
	 * The processor for every instance of reading.
	 */
	private class MainProcessorIn implements BindingLayoutProcessor {
		
		@Override
		public void process(Layout node) {
			
			bean = type.newInstance();
		}
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.dido.bio.SingleBeanBinding#inject(java.lang.Object, org.oddjob.dido.Layout, org.oddjob.dido.DataOut)
	 */
	@Override
	protected void inject(Object object, Layout node, DataOut dataOut) throws DataException {
		
		if (type != null && !type.forClass().isInstance(object)) {
			
			logger.trace("Binding on [" + node + "] ignoring bean " + 
					object);
			
			return;
		}
		
		this.bean = object;
		
		if (processor == null) {
			if (type == null) {

				type = accessor.getClassName(object);
				
				logger.debug("Binding on [" + node + "] using type of first bean which is " + 
						type);
				
				resets.add(new Runnable() {
					@Override
					public void run() {
						type = null;
					}
				});
			}
			
			if (node instanceof Morphable) {
				
				logger.debug("Giving " + node + " the opportunity to morph.");
				
				Runnable reset = ((Morphable) node).morphInto(
						new MorphDefinitionFactory(accessor).readableMorphMetaDataFor(
								type, beanView));
				
				resets.add(reset);
			}
			
			processor = new HeaderProcessorOut();
		}
		
		processor.process(node);
	
		DataWriter nextWriter = new ChildWriter(node.childLayouts(), 
					dataOut);
		
		nextWriter.write(object); 
		
		nextWriter.close();
	}
	
	/**
	 * The initial processor that binds to children for writing values
	 * from layout nodes from the readable properties of the bean.
	 */
	private class HeaderProcessorOut implements BindingLayoutProcessor {
			
		@Override
		public void process(final Layout parentLayout) {
			
			final BeanOverview overview = 
					accessor.getClassName(bean).getBeanOverview(accessor);
			
			logger.debug("Binding for [" + parentLayout + "] binding to children.");
			
			new LayoutWalker() {
				
				@Override
				protected boolean onLayout(final Layout layout) {
					
					final String nodeName = layout.getName();
					
					if (nodeName == null) {
						
						logger.debug("Binding ignoring node [" + layout + 
								"] as it has no name.");

						return true;
					}
					
					if (overview.hasReadableProperty(nodeName)) {
						
						layout.bind(new ChildNodeBinding(layout));
					
						logger.debug("Binding to child [" + layout + 
								"] on readable property [" + nodeName + "]");
						
						resets.add(new Runnable() {
							@Override
							public void run() {
								layout.bind(null);
							}
						});
						
						return false;
					}
					else {
						
						logger.debug("Binding ignoring [" + layout + 
								"] as there is no simple readable property [" +
								nodeName + "]");
						
						return true;
					}
				}
			}.walkChildren(parentLayout);
			
			processor = new MainProcessorOut();
			processor.process(parentLayout);
		}
	}
	
	/**
	 * The processor for every instance of writing.
	 */
	private class MainProcessorOut implements BindingLayoutProcessor {
		
		@Override
		public void process(Layout node) {
			logger.trace("Binding on layout [" + node  + 
					"] is binding bean [" + bean + "]");
		}
	}

	@Override
	public void free() {
		processor = null;
		resets.reset();
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
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + 
				(type == null ? " for an unknown type" : type.toString());
	}
}
