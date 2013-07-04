package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.DataWriterFactory;

/**
 * 
 * @author rob
 *
 */
public class ChildWriter implements DataWriter {

	private static final Logger logger = Logger.getLogger(ChildWriter.class);

	private final List<DataWriter> writers;
	
	private boolean closed;
	
	public ChildWriter(Iterable<? extends DataWriterFactory> children,
			DataOut dataOut) throws DataException {
		
		this.writers = new ArrayList<DataWriter>();
		
		for (DataWriterFactory factory : children) {
			writers.add(factory.writerFor(dataOut));
		}
	}	
	
	@Override
	public boolean write(Object object) throws DataException {
		
		if (closed) {
			throw new IllegalStateException("Writer closed.");
		}

		if (writers.size() == 0) {			
			return false;
		}

		DataWriter currentWriter = writers.get(0);

		boolean keep = currentWriter.write(object);
		
		if (keep) {
			return true;
		}
		else {
			currentWriter.close();
			writers.remove(0);
			
			return write(object);
		}
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
}
