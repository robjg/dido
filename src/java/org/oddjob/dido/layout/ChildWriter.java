package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.DataWriterFactory;

/**
 * A writer for a list writers obtained from a list of 
 * {@link DataWriterFactory}s. Typically these will be children of some
 * parent Layout.
 * <p>
 * 
 * @author rob
 *
 */
public class ChildWriter implements DataWriter {

	private static final Logger logger = Logger.getLogger(ChildWriter.class);

	/** The writers. */
	private final List<DataWriter> writers;
	
	/** Closed flag. */
	private boolean closed;
	
	/**
	 * Create a new instance. This creates all the writer from the 
	 * factories ensuring they are all initialised before any writing
	 * happens.
	 * 
	 * @param children
	 * @param dataOut
	 * @throws DataException
	 */
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

		while (writers.size() > 0) {			

			DataWriter currentWriter = writers.get(0);

			boolean keep = currentWriter.write(object);

			if (keep) {
				if (logger.isTraceEnabled()) {
					logger.trace("Current writer [" + currentWriter + 
							"] requires more data.");
				}

				return true;
			}
			else {
				if (logger.isTraceEnabled()) {
					logger.trace("Current writer [" + currentWriter + "] complete.");
				}

				currentWriter.close();
				writers.remove(0);
			}
		}
		
		return false;
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
		if (closed) {
			return getClass().getSimpleName() + " closed";
		}
		else {
			return getClass().getSimpleName() + " for " + writers;
		}
	}
}
