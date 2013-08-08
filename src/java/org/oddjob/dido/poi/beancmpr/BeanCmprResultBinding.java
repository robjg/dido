package org.oddjob.dido.poi.beancmpr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.bio.Resets;
import org.oddjob.dido.bio.SingleBeanBinding;
import org.oddjob.dido.layout.ChildWriter;
import org.oddjob.dido.morph.MorphDefinition;
import org.oddjob.dido.morph.MorphDefinitionBuilder;
import org.oddjob.dido.morph.Morphable;
import org.oddjob.dido.poi.layouts.DataCell;
import org.oddjob.dido.poi.style.DefaultStyleProivderFactory;

public class BeanCmprResultBinding extends SingleBeanBinding
implements ArooaSessionAware {

	private final Resets resets = new Resets();
	
	private PropertyAccessor accessor;
	
	private ArooaConverter converter;
	
	private String xPrefix = "X";
	
	private String yPrefix = "Y";
	
	boolean initialised;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.accessor = session.getTools().getPropertyAccessor();
		this.converter = session.getTools().getArooaConverter();
	}
	
	@Override
	protected Object extract(Layout boundLayout, DataIn dataIn)
			throws DataException {
		
		throw new UnsupportedOperationException("Can't Read yet.");
	}
	
	@Override
	protected void inject(Object object, Layout boundLayout, DataOut dataOut)
			throws DataException {
		
		ResultBeanWrapper result = new ResultBeanWrapper(accessor, object);

		if (!initialised) {
			
			Initialiser initialiser = new Initialiser(result);
			
			if (boundLayout instanceof Morphable) {
				resets.add(((Morphable) boundLayout).morphInto(
						initialiser.morphDefinition));
			}
			
			int i = 0;
			for (final Layout layout : boundLayout.childLayouts()) {
				layout.bind(new ChildBinding(initialiser.injectors.get(i++)));
				resets.add(new Runnable() {
					@Override
					public void run() {
						layout.bind(null);
					}
				});
			}
			
			initialised = true;
		}		
		
		new ChildWriter(boundLayout.childLayouts(), dataOut).write(result);
	}
	
	class ChildBinding extends SingleBeanBinding {
		
		private final Injector injector;
		
		public ChildBinding(Injector injector) {
			this.injector = injector;
		}
		
		@Override
		protected Object extract(Layout boundLayout, DataIn dataIn)
				throws DataException {
			return null;
		}
		
		@Override
		protected void inject(Object object, Layout boundLayout, DataOut dataOut)
				throws DataException {
			injector.injectInto((ValueNode<?>) boundLayout, (ResultBeanWrapper) object);
		}
		
		@Override
		public void free() {
		}
	}
	
	private <T> void doSetValue(ValueNode<T> valueNode, Object value)
	throws DataException {
		try {
			@SuppressWarnings("unchecked")
			Class<T> type = (Class <T>) valueNode.getType();
			
			valueNode.value(converter.convert(value, type));
		} 
		catch (NoConversionAvailableException e) {
			throw new DataException(e);
		} 
		catch (ConversionFailedException e) {
			throw new DataException(e);
		}
	}
	
	interface Injector {
		
		void injectInto(ValueNode<?> valueNode, ResultBeanWrapper bean)
		throws DataException;
	}
	
	class ResultInjector implements Injector {
		
		@Override
		public void injectInto(ValueNode<?> valueNode, ResultBeanWrapper bean) 
		throws DataException {
			
			int resultType = bean.getResultType();
			String typeAsText;
			switch (resultType) {
			case 0:
				typeAsText = "EQUALS";
				break;
			case 1:
				typeAsText = xPrefix + " MISSING";
				break;
			case 2:
				typeAsText = yPrefix + " MISSING";
				break;
			case 3:
				typeAsText = "DIFFERENT";
				break;
			default:
				throw new IllegalStateException("Unknown Result Type.");
			}
			doSetValue(valueNode, typeAsText);
		}
	}
	
	class KeyInjector implements Injector {
		
		final String keyName;

		public KeyInjector(String keyName) {
			this.keyName = keyName;
		}
		
		@Override
		public void injectInto(ValueNode<?> valueNode, ResultBeanWrapper bean) 
		throws DataException {
			
			doSetValue(valueNode, bean.getKeys().get(keyName));
		}
	}
	
	abstract class ComparisonInjector implements Injector {
		
		final String comparisonName;

		public ComparisonInjector(String comparisonName) {
			this.comparisonName = comparisonName;
		}
		
		@Override
		public void injectInto(ValueNode<?> valueNode, ResultBeanWrapper bean) 
		throws DataException {
			
			ComparisonBeanWrapper comparison = bean.getComparisons().get(comparisonName);
			
			doSetValue(valueNode, get(comparison));
			
			if (comparison.getResult() != 0 && valueNode instanceof DataCell) {
				((DataCell<?>) valueNode).setStyle(
						DefaultStyleProivderFactory.BEANCMPR_DIFF_STYLE);
			}
			else {
				((DataCell<?>) valueNode).setStyle(
						DefaultStyleProivderFactory.BEANCMPR_MATCH_STYLE);
			}
		}
		
		abstract Object get(ComparisonBeanWrapper comparison);
	}
	
	class ComparisonXInjector extends ComparisonInjector {
		
		public ComparisonXInjector(String comparisonName) {
			super(comparisonName);
		}
		
		@Override
		Object get(ComparisonBeanWrapper comparison) {
			return comparison.getX();
		}
	}
	
	class ComparisonYInjector extends ComparisonInjector {
		
		public ComparisonYInjector(String comparisonName) {
			super(comparisonName);
		}
		
		@Override
		Object get(ComparisonBeanWrapper comparison) {
			return comparison.getY();
		}
	}
	
	class Initialiser {
		
		MorphDefinition morphDefinition;

		List<Injector> injectors = new ArrayList<Injector>();

		public Initialiser(ResultBeanWrapper result) {
		
			MorphDefinitionBuilder morphBuilder = new MorphDefinitionBuilder();
			
			morphBuilder.add("resultType", "Result Type", String.class);
			injectors.add(new ResultInjector());
			
			Map<String, Object> keys = result.getKeys();
			
			for (Map.Entry<String, Object> entry : keys.entrySet()) {
				String key = entry.getKey();
				morphBuilder.add(key, typeFor(entry.getValue()));
				injectors.add(new KeyInjector(key));
			}
			
			Map<String, ComparisonBeanWrapper> comparisons = result.getComparisons();
			
			for (Map.Entry<String, ComparisonBeanWrapper> entry : comparisons.entrySet()) {
				
				String comparisonName = entry.getKey();
				String xProperty = xPrefix + "_" + comparisonName;
				String yProperty = yPrefix + "_" + comparisonName;

				ComparisonBeanWrapper comparison = entry.getValue();
				
				morphBuilder.add(xProperty,
							typeForClass(comparison.getTypeOfY()));
				morphBuilder.add(yProperty, 
						typeForClass(comparison.getTypeOfY()));
				
				injectors.add(new ComparisonXInjector(comparisonName));
				injectors.add(new ComparisonYInjector(comparisonName));
			}
			
			morphDefinition = morphBuilder.build();
		}
	}	
	
	static Class<?> typeFor(Object key) {
		return typeForClass(key.getClass());
	}
	
	static Class<?> typeForClass(Class<?> type) {
				
		if (type.isPrimitive()) {
			return type;
		}
		
		if (Date.class.isAssignableFrom(type)) {
			return Date.class;
		}
		
		if (Boolean.class == type) {
			return Boolean.class;
		}

		if (Byte.class == type || Short.class == type || 
				Integer.class == type || Long.class == type ||
				BigInteger.class == type) {
			
			return Long.class;
		}
		
		if (Number.class.isAssignableFrom(type)) {
			return Number.class;
		}
		
		return String.class;
	}
	
	
	@Override
	public void free() {
		resets.reset();
		initialised = false;
	}

	public String getxPrefix() {
		return xPrefix;
	}

	public void setxPrefix(String xPrefix) {
		this.xPrefix = xPrefix;
	}

	public String getyPrefix() {
		return yPrefix;
	}

	public void setyPrefix(String yPrefix) {
		this.yPrefix = yPrefix;
	}
}
