package org.oddjob.dido.other;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;
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
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(final DataIn dataIn) throws DataException {
		
		final Layout descriminator = childLayouts().get(0);
		
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
	
	private Layout evaluate() {
				
		for (int i = 1; i < childLayouts().size(); ++i) {

			Layout child = childLayouts().get(i);
			
			if (((CaseCondition<TYPE>) child).evaluate(value)) {
				return child;
			}
		}
		
		return null;
	}
	
	@Override
	public DataWriter writerFor(final DataOut dataOut) throws DataException {
	
		final Layout descriminator = childLayouts().get(0);
		
		return new DataWriter() {
			
			DataWriter nextWriter;
			
			@Override
			public boolean write(Object object) throws DataException {
				
				if (nextWriter == null) {
					
					Layout chosen = evaluateOut();
					
					if (chosen == null) {
						return false;
					}
					else {
						((ValueNode<TYPE>) descriminator).value(
								value);
						
						DataWriter descriminatorWriter = 
								descriminator.writerFor(dataOut);
						
						descriminatorWriter.write(object);
						
						nextWriter = chosen.writerFor(dataOut);
					}
				}
				
				if (nextWriter.write(object)) {
					
					return true;
				}

				nextWriter = null;
				
				return false;
			}
		};
		
	}

	private Layout evaluateOut() {
		
		for (int i = 1; i < childLayouts().size(); ++i) {
			Layout child = childLayouts().get(i);
			
			TYPE value = ((CaseCondition<TYPE>) child).evaluateOut();
			
			if (value != null) {
				this.value = value;
				return child;
			}
			
		}
		return null;
	}
	
	@Override
	public void reset() {
	}
	
	@Override
	public void changeValue(TYPE value) {
		((ValueNode<TYPE>) childLayouts().get(0)).value(value);
	}

}
