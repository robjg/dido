package org.oddjob.dido.other;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.bio.ValueBinding;
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
public class Case<TYPE>
extends LayoutNode
implements Changeable<TYPE>{

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
		
		return new DataReader() {

			DataReader nextReader;
			
			@Override
			public Object read() throws DataException {
				
				if (nextReader == null) {
					
					DataReader descriminatorReader = descriminator.readerFor(dataIn);
					
					descriminatorReader.read();
					
					Case.this.value = ((ValueNode<TYPE>) descriminator).value();
					
					Layout chosen = evaluate();
					
					if (chosen == null) {
						throw new DataException("No option to read [" + 
								Case.this.value + "]");
					}
	
					nextReader = chosen.readerFor(dataIn);
				}
				
				Object value = nextReader.read();
				if (value == null) {
					nextReader = null;
				}
				
				return value;
			}
		};
	}
	
	@Override
	public void bind(Binding binding) {
		throw new UnsupportedOperationException("Binding not supported on Case.");
	}
	
	private Layout evaluate() {
				
		for (int i = 1; i < childLayouts().size(); ++i) {

			Layout child = childLayouts().get(i);
			
			if (((CaseCondition<TYPE>) child).evaluate(value)) {
				return child;
			}
		}
		
		return null;
	}

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
			
			descriminator.bind(new ValueBinding());
			if (resets.size() == 0) {
				resets.add(new Runnable() {
					@Override
					public void run() {
						descriminator.bind(null);
					}
				});
			
			}
		}
	}
	
	private WriterInitialisation initialisation;
	
	@Override
	public DataWriter writerFor(final DataOut dataOut) throws DataException {
	
		if (initialisation == null) {
			initialisation = new WriterInitialisation();
		}
		
		return new DataWriter() {
				
			WhenWriter<TYPE> nextWriter;
			
			@Override
			public boolean write(Object object) throws DataException {
				
				if (nextWriter == null) {
					
					nextWriter = new WhenWriter<TYPE>(
							initialisation.whens, null, dataOut);
				}
				
				if (nextWriter.write(object)) {
					
					return true;
				}

				value = nextWriter.value;
				
				if (value != null) {

					DataWriter descriminatorWriter = 
							initialisation.descriminator.writerFor(
									dataOut);
					
					descriminatorWriter.write(value);
				}
				
				nextWriter = null;
				
				return false;
			}
		};
	}
	
	@Override
	public void reset() {
		for (Runnable reset : resets) {
			reset.run();
		}
		initialisation = null;
	}
	
	@Override
	public void changeValue(TYPE value) {
		((ValueNode<TYPE>) childLayouts().get(0)).value(value);
	}

	private class WhenWriter<T> implements DataWriter {

		private final Iterator<? extends Layout> iterator;
		
		private final DataOut dataOut;
		
		private Layout current;
		
		private DataWriter writer;
				
		private T value;
		
		public WhenWriter(Iterable<? extends Layout> children,
				ValueNode<?> parent, DataOut dataOut) {
			
			iterator = children.iterator();
			this.dataOut = dataOut;
		}	
		
		@Override
		public boolean write(Object object) throws DataException {

			if (writer == null) {
				
				if (iterator.hasNext()) {
					current = iterator.next();
				}
				else {
					return false;
				}
				
				if (current == null) {
					
					return false;
				}
				
				writer = current.writerFor(dataOut);				
			}

			if (writer.write(object)) {
				return true;
			}
			
			if (dataOut.hasData()) {
				
				this.value = ((CaseCondition<T>) current).value();
				
				return false;
			}
			else {
				current = null;
				writer = null;
				
				return write(object);
			}
		}
	}

}
