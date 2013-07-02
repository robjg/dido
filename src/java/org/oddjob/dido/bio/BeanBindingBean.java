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
 * At the moment property names must match node names, but this will change 
 * soon.
 * 
 * @author rob
 *
 */
public class BeanBindingBean extends SingleBeanBinding
implements Binding, ArooaSessionAware {

	private static final Logger logger = Logger.getLogger(BeanBindingBean.class);
	
	/**
	 * PropertyAccessor with conversions.
	 */
	private PropertyAccessor accessor;
	
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
		protected boolean inject(Object value, 
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
		public void free() {
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
	
	@Override
	public void free() {
		processor = null;
		for (Runnable reset : resets) {
			reset.run();
		}
		resets.clear();
	}
	
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
					
					if (nodeName != null && 
							overview.hasWriteableProperty(nodeName)) {
						
						layout.bind(new ChildNodeBinding(layout));
					
						logger.debug("Binding for [" + parentLayout + 
								"] binding to child [" + layout + "]");
						
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
			}.walk(parentLayout);
			
			processor = new MainProcessorOut();
			processor.process(parentLayout);
		}
	}
	
	private class MainProcessorOut implements BindingLayoutProcessor {
		
		@Override
		public void process(Layout node) {
			logger.trace("Binding on layout [" + node  + 
					"] is binding bean [" + bean + "]");
		}
	}

	@Override
	public boolean inject(Object object, Layout node, DataOut dataOut) throws DataException {
		
		if (type != null && !type.forClass().isInstance(object)) {
			
			logger.trace("Binding on [" + node + "] ignoring bean " + 
					object);
			
			return false;
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
					node instanceof ValueNode ? (ValueNode<?>) node : null, 
							dataOut);
		
		nextWriter.write(object); 
		
		nextWriter.close();
		
		return false;
		
	}
		
	private void derriveTypeFromLayout(Layout layout) throws DataException {
		
		final MagicBeanClassCreator classCreator = 
				new MagicBeanClassCreator("BeanBinding");

		List<Layout> children = layout.childLayouts();
		
		if (children.size() > 0) {
			
			logger.debug("Deriving Magic Bean Properties from child layouts.");
			
			new LayoutWalker() {				
				@Override
				protected boolean onLayout(Layout layout) {
					if (layout.childLayouts().size() == 0 && 
							layout.getName() != null &&
							layout instanceof ValueNode) {
						
						String propertyName = layout.getName();
						Class<?> propertyType = 
								((ValueNode<?>) layout).getType();
						
						logger.debug("Adding property " + propertyName + 
								" of type " + propertyType.getName());
						
						classCreator.addProperty(propertyName, 
								propertyType);
					}
					
					return true;
				}
			}.walk(layout);
			
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
				
				classCreator.addProperty(propertyName, 
						propertyType);
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
