package org.oddjob.dido.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.layout.LayoutNode;

/**
 * @oddjob.description Specify alternative data plans depending on a 
 * data value.
 * <p>
 * See also {@link When}.
 * 
 * @author rob
 *
 * @param <TYPE>
 */
public class Case<TYPE> extends LayoutNode {

	private static final Logger logger = Logger.getLogger(Case.class);
	
	private TYPE value;
	
	private final List<Runnable> resets = new ArrayList<Runnable>();
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(final DataIn dataIn) throws DataException {
		
		final Layout descriminator = childLayouts().get(0);
		
		if (descriminator == null) {
			throw new NullPointerException(
					"No descriminator layout for Case.");
		}
		
		if (! (descriminator instanceof ValueNode)) {
			throw new DataException(
					"Descriminator is not a ValueNode.");
			
		}
		
		return new DataReader() {

			DataReader nextReader;
			
			@SuppressWarnings("unchecked")
			@Override
			public Object read() throws DataException {
				
				if (nextReader == null) {
					
					DataReader descriminatorReader = descriminator.readerFor(dataIn);
					
					descriminatorReader.read();
					
					Case.this.value = ((ValueNode<TYPE>) descriminator).value();
					
					logger.trace("Read descriminator value of [" + 
							Case.this.value + "]");
					
					Layout chosen = evaluateLayoutForReading();
					
					if (chosen == null) {
						throw new DataException("No option to read [" + 
								Case.this.value + "]");
					}
	
					logger.trace("Chosen Layout [" + chosen + "]");
					
					nextReader = chosen.readerFor(dataIn);
				}
				
				Object value = nextReader.read();
				
				if (value == null) {
					nextReader.close();
					nextReader = null;
				}
				
				return value;
			}
			
			@Override
			public void close() throws DataException {
				if (nextReader != null) {
					nextReader.close();
				}
			}
		};
	}
	
	/**
	 * Utility method to evaluate the Layout to use for reading.
	 * 
	 * @return The layout that matches the value.
	 */
	@SuppressWarnings("unchecked")
	private Layout evaluateLayoutForReading() {
				
		for (int i = 1; i < childLayouts().size(); ++i) {

			Layout child = childLayouts().get(i);
			
			if (! (child instanceof CaseCondition)) {
				throw new IllegalStateException(
					"All the children of the Case but the first must be of type " +
					CaseCondition.class.getName());	
			}
			
			if (((CaseCondition<TYPE>) child).evaluate(value)) {
				return child;
			}
		}
		
		return null;
	}

	/**
	 * Initialise the Writer.
	 * 
	 */
	private class WriterInitialisation {
		
		private List<Layout> whens;
		
		private Layout descriminator;
		
		WriterInitialisation() {
			
			List<Layout> childLayouts = childLayouts();
			
			whens = new ArrayList<Layout>();
			
			for (int i = 1; i < childLayouts.size(); ++i) {
				
				Layout when = childLayouts.get(i);
						
				if (! (when instanceof CaseCondition)) {
					throw new IllegalStateException("Children must be Case Conditions.");
				}
				
				whens.add(when);
				
			}
	
			descriminator = childLayouts.get(0);
			
			if (descriminator == null) {
				throw new NullPointerException(
						"No descriminator layout for Case.");
			}
			
			descriminator.bind(new DirectBinding());
			
			if (resets.size() == 0) {
				resets.add(new Runnable() {
					
					@Override
					public void run() {
						
						logger.trace("Removing Case binding from descriminator [" + 
								descriminator + "]");
						
						descriminator.bind(null);
					}
				});
			}
			
			logger.trace("Initialised writing with " + whens.size() + 
					" whens and descriminator [" + descriminator + "]");
		}
	}
	
	class MainWriter implements DataWriter {
		
		private final WriterInitialisation initialisation;
		
		private final DataOut dataOut;
		
		private final WhenWriter<TYPE> whenWriter;
	
		public MainWriter(WriterInitialisation initialisation,
				DataOut dataOut) {
			
			this.initialisation = initialisation;
			this.dataOut = dataOut;
			
			this.whenWriter = new WhenWriter<TYPE>(initialisation.whens);
		}
		
		@Override
		public boolean write(Object object) throws DataException {
						
			if (!whenWriter.write(object, dataOut)) {
				
				whenWriter.close();
				
				return false;
			}
			
			value = whenWriter.value;
			
			if (value != null) {
		
				DataWriter descriminatorWriter = 
						initialisation.descriminator.writerFor(
								dataOut);
				
				descriminatorWriter.write(value);
			}
			
			return true;
		}
	
		@Override
		public void close() throws DataException {
			
			whenWriter.close();
		}
	}

		
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {

		logger.trace("Creating writer for [" + dataOut + "]");
		
		return new MainWriter(new WriterInitialisation(), dataOut);
	}
	
	@Override
	public void reset() {
		for (Runnable reset : resets) {
			reset.run();
		}
	}
	
	/**
	 * Writes Data for the {@link CaseCondition}s.
	 * <p>
	 * The methodology is: Iterate over children writing out data until
	 * one of them writes data. If one writes data then set value
	 * of this case to the value of the {@code CaseCondition} that
	 * wrote the data. This will then be used to write the descriminator.
	 * 
	 * @param <T>
	 */
	private class WhenWriter<T> {

		private final Iterable<? extends Layout> children;
		
		private final Map<Layout, DataWriter> openWriters = 
				new HashMap<Layout, DataWriter>();
		
		private T value;
		
		public WhenWriter(Iterable<? extends Layout> children) {
			
			this.children = children;
		}	
		
		@SuppressWarnings("unchecked")
		public boolean write(Object object, DataOut dataOut) throws DataException {

			this.value = null;
			
			for (Layout currentLayout : children) {
			
				DataWriter currentWriter = openWriters.get(currentLayout);
			
				
				if (currentWriter == null) {
					currentWriter = currentLayout.writerFor(dataOut);
				}
				
				boolean keep = currentWriter.write(object);
				
				if (keep) {
					openWriters.put(currentLayout, currentWriter);
				}
				else {
					currentWriter.close();
					openWriters.remove(currentLayout);
				}
			
				if (dataOut.hasData() || keep) {
				
					this.value = ((CaseCondition<T>) currentLayout).value();
				
					logger.trace("CaseCondition [" + currentLayout + 
							"] provided data [" + value + "]");

					return true;
				}
			}
			
			logger.debug("No children wrote data for " + Case.this);
			
			return false;
		}		
		
		void close() throws DataException {
			
			if (openWriters.size() > 0) {
				
				logger.trace("Closing open writers.");
				
				for (DataWriter writer : openWriters.values()) {
					writer.close();
				}
			}
		}
	}
	
	@Override
	public void bind(Binding binding) {
		throw new UnsupportedOperationException("Binding not supported on Case.");
	}	
	
	@Override
	protected DataReader nextReaderFor(DataIn dataIn) {
		throw new UnsupportedOperationException(
				"Case must handle its own child reading.");
	}
	
	@Override
	protected DataWriter nextWriterFor(DataOut dataOut) {
		throw new UnsupportedOperationException(
				"Case must handle its own child writing.");
	}
}
