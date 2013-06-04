package org.oddjob.dido.layout;

import java.util.Iterator;

import org.apache.log4j.Logger;
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

	private static final Logger logger = Logger.getLogger(ChildWriter.class);
	
	private final Iterator<? extends DataWriterFactory> iterator;
	
	private final DataOut dataOut;
	
	private final ValueNode<?> valueNode;
	
	private DataWriter currentWriter;
	
	private boolean closed;
	
	public ChildWriter(Iterable<? extends DataWriterFactory> children,
			ValueNode<?> parent, DataOut dataOut) {
		iterator = children.iterator();
		this.valueNode = parent;
		this.dataOut = dataOut;
	}	
	
	@Override
	public boolean write(Object object) throws DataException {
		
		if (closed) {
			throw new IllegalStateException("Writer closed.");
		}
		
		if (currentWriter == null && iterator.hasNext()) {
			currentWriter = iterator.next().writerFor(dataOut);
		}
		
		if (currentWriter == null) {
			
			writeDataWithInferredType(valueNode);
			
			return false;
		}

		boolean keep = currentWriter.write(object);
		
		if (keep) {
			writeDataWithInferredType(valueNode);
			
			return true;
		}
		else {
			currentWriter.close();
			currentWriter = null;
				
			return write(object);
		}
	}
	
	private <T> void writeDataWithInferredType(ValueNode<T> valueNode) {
		
		if (valueNode == null) {
			return;
		}
			
		T value = dataOut.toValue(valueNode.getType());
		
		logger.trace("Writing [" + value + "] from [" + dataOut + 
				"] back to [" + valueNode + "]");
		
		valueNode.value(value);
	}
	
	@Override
	public void close() throws DataException {
		if (currentWriter != null) {
			
			logger.trace("Closing [" + currentWriter + "]");
			
			currentWriter.close();
			currentWriter = null;
		}
		
		closed = true;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + (valueNode == null ? "" :
			" for [" + valueNode + "]");
	}
}
