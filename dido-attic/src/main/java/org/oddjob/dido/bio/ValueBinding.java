package org.oddjob.dido.bio;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.Layout;
import org.oddjob.dido.ValueNode;

/**
 * A very simple {@link Binding} that uses the fixed value attribute as
 * the binding onto a single {@link Layout} that is assumed to be a 
 * {@link valueNode}.
 * 
 * @author rob
 *
 */
public class ValueBinding extends SingleBeanBinding
implements Binding, ArooaSessionAware {

	private String value;
	
	private ArooaConverter converter;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.converter = session.getTools().getArooaConverter();
	}
	
	@Override
	protected Object extract(Layout node, DataIn dataIn) 
	throws DataException {

		if (node instanceof ValueNode) {
			
			@SuppressWarnings("unchecked")
			ValueNode<Object> valueNode = (ValueNode<Object>) node;
			
			Class<?> type = valueNode.getType();
			
			try {
				return converter.convert(value, type);
			} catch (Exception e) {
				throw new DataException(e);
			}
		}
		else {
			throw new IllegalStateException("Not a Value Node");
		}
	}
	
	@Override
	protected void inject(Object object, Layout node, DataOut dataOut) throws DataException {
		
		if (node instanceof ValueNode) {
			
			@SuppressWarnings("unchecked")
			ValueNode<Object> valueNode = (ValueNode<Object>) node;
			Class<?> type = valueNode.getType();
			
			try {
				valueNode.value(converter.convert(value, type));
			} 
			catch (Exception e) {
				throw new DataException(e);
			}
		}
		else {
			throw new IllegalStateException("Not a Value Node");
		}
	}
	
	@Override
	public void free() {
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + ": " +
				(value == null ? "(null)" : value);
	}
}
