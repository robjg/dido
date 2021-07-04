package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataReaderFactory;

/**
 * A reader for a list readers obtained from a list of 
 * {@link DataReaderFactory}s. Typically these will be children of some
 * parent Layout.
 * <p>
 * 
 * @author rob
 *
 */
public class ChildReader implements DataReader {

	private static final Logger logger = Logger.getLogger(ChildReader.class);

	/** The readers. */
	private final List<DataReader> readers;
	
	/** closed flag. */
	private boolean closed;
	
	/**
	 * Create a new instance. This creates all the readers from the 
	 * factories ensuring they are all initialised before any reading 
	 * happens.
	 * 
	 * @param children
	 * @param dataIn
	 * @throws DataException
	 */
	public ChildReader(Iterable<? extends DataReaderFactory> children,
			DataIn dataIn) throws DataException {
		
		readers = new ArrayList<DataReader>();
		for (DataReaderFactory factory : children) {
			readers.add(factory.readerFor(dataIn));
 		}
	}	
	
	@Override
	public Object read() throws DataException {
		
		if (closed) {
			throw new IllegalStateException("Reader closed.");
		}

		while (readers.size() > 0) {
		
			DataReader currentReader = readers.get(0);

			Object value = currentReader.read();

			if (value == null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Current reader [" + currentReader + "] complete.");
				}

				currentReader.close();
				readers.remove(0);
			}
			else {
				if (logger.isTraceEnabled()) {
					logger.trace("Current reader [" + currentReader + 
							"] provided value [" + value + "]");
				}

				return value;
			}
		}
		
		return null;
	}
	
	@Override
	public void close() throws DataException {
		
		for (DataReader currentReader : readers) {
			currentReader.close();
			logger.trace("Closing [" + currentReader + "]");
		}
		
		readers.clear();
		closed = true;
	}
	
	@Override
	public String toString() {
		if (closed) {
			return getClass().getSimpleName() + ", closed";
		}
		else {
			return getClass().getSimpleName() +  " for " + readers;
		}
	}
}
