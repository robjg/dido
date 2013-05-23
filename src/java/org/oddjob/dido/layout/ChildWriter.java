package org.oddjob.dido.layout;

import java.util.Iterator;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.DataWriterFactory;
import org.oddjob.dido.ValueNode;

/**
 * 
 * @author rob
 *
 */
public class ChildWriter implements DataWriter {

	private final Iterator<? extends DataWriterFactory> iterator;
	
	private final DataOut dataOut;
	
	private final ValueNode<?> valueNode;
	
	private DataWriter current;
	
	public ChildWriter(Iterable<? extends DataWriterFactory> children,
			ValueNode<?> parent, DataOut dataOut) {
		iterator = children.iterator();
		this.valueNode = parent;
		this.dataOut = dataOut;
	}	
	
	@Override
	public boolean write(Object value) throws DataException {
		
		if (current == null && iterator.hasNext()) {
			current = iterator.next().writerFor(dataOut);
		}
		
		if (current == null) {
			
			if (valueNode != null) {
				writeDataWithInferredType(valueNode);
			}
			return false;
		}

		if (current.write(value)) {
			return true;
		}
		else {
			current = null;
			return write(value);
		}
	}
	
	private <T> void writeDataWithInferredType(ValueNode<T> valueNode) {
		valueNode.value(dataOut.toValue(valueNode.getType()));
	}
}
