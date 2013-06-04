package org.oddjob.dido.layout;

import java.util.Iterator;

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

	private final Iterator<? extends DataReaderFactory> iterator;
	
	private final DataIn dataIn;
	
	private DataReader currentReader;
	
	public ChildReader(Iterable<? extends DataReaderFactory> children,
			DataIn dataIn) {
		iterator = children.iterator();
		this.dataIn = dataIn;
	}	
	
	@Override
	public Object read() throws DataException {
		
		if (currentReader == null && iterator.hasNext()) {
			currentReader = iterator.next().readerFor(dataIn);
		}
		
		if (currentReader == null) {
			return null;
		}

		Object value = currentReader.read();
		if (value == null) {
			currentReader.close();
			currentReader = null;
			return read();
		}
		else {
			return value;
		}
	}
	
	@Override
	public void close() throws DataException {
		if (currentReader != null) {
			currentReader.close();
		}
	}
}
