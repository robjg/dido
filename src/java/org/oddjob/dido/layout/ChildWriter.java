package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataValueOut;
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

	private final List<DataWriter> writers;
	
	private final DataOut dataOut;
	
	private final ValueNode<?> valueNode;
	
	private boolean closed;
	
	public ChildWriter(Iterable<? extends DataWriterFactory> children,
			ValueNode<?> parent, DataOut dataOut) throws DataException {
		
		this.writers = new ArrayList<DataWriter>();
		
		for (DataWriterFactory factory : children) {
			writers.add(factory.writerFor(dataOut));
		}

		this.valueNode = parent;
		this.dataOut = dataOut;
		
		logger.trace("Created ChildWriter for [" + valueNode + 
				"] parent with " + writers.size() + " children.");
	}	
	
	@Override
	public boolean write(Object object) throws DataException {
		
		if (closed) {
			throw new IllegalStateException("Writer closed.");
		}

		if (writers.size() == 0) {
			writeDataWithInferredType(valueNode);
			
			return false;
		}

		DataWriter currentWriter = writers.get(0);

		boolean keep = currentWriter.write(object);
		
		if (keep) {
			writeDataWithInferredType(valueNode);
			
			return true;
		}
		else {
			currentWriter.close();
			writers.remove(0);
			
			return write(object);
		}
	}
	
	private <T> void writeDataWithInferredType(ValueNode<T> valueNode) {
		
		if (dataOut == null) {
			return;
		}
		
		if (valueNode == null) {
			return;
		}
		
		if (! (dataOut instanceof DataValueOut)) {
			return;
		}
		
		T value = ((DataValueOut) dataOut).toValue(valueNode.getType());
		
		logger.trace("Writing [" + value + "] from [" + dataOut + 
				"] back to [" + valueNode + "]");
		
		valueNode.value(value);
	}
	
	@Override
	public void close() throws DataException {
		
		for (DataWriter currentWriter : writers) {
				
			logger.trace("Closing [" + currentWriter + "]");
			currentWriter.close();
		}
		
		writers.clear();
		closed = true;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + (valueNode == null ? "" :
			" for [" + valueNode + "]");
	}
}
