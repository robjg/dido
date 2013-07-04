package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataReaderFactory;

/**
 * 
 * @author rob
 *
 */
public class ChildReader implements DataReader {

	private static final Logger logger = Logger.getLogger(ChildReader.class);

	private final List<DataReader> readers;;
	
	private boolean closed;
	
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

		if (readers.size() == 0) {
			return null;
		}
		
		DataReader currentReader = readers.get(0);

		Object value = currentReader.read();
		
		if (value == null) {
			currentReader.close();
			readers.remove(0);

			return read();
		}
		else {
			return value;
		}
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
}
