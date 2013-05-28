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
import org.oddjob.dido.Headed;
import org.oddjob.dido.Layout;
import org.oddjob.dido.MorphicnessFactory;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.io.ClassMorphic;
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
			logger.info("Binding data to " + node.getName());
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
