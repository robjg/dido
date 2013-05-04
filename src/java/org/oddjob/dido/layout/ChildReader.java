package org.oddjob.dido.layout;

import java.util.Iterator;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataInProvider;
import org.oddjob.dido.ReaderFactory;
import org.oddjob.dido.io.DataReader;

/**
 * 
 * @author rob
 *
 */
public class ChildReader implements DataReader {

	private final Iterator<? extends ReaderFactory> iterator;
	
	private final DataInProvider dataIn;
	
	private DataReader current;
	
	public ChildReader(Iterable<? extends ReaderFactory> children,
			DataInProvider dataIn) {
		iterator = children.iterator();
		this.dataIn = dataIn;
	}	
	
	@Override
	public Object read() throws DataException {
		
		if (current == null && iterator.hasNext()) {
			current = iterator.next().readerFor(dataIn);
		}
		
		if (current == null) {
			return null;
		}

		Object value = current.read();
		if (value == null) {
			current = null;
			return read();
		}
		else {
			return value;
		}
	}
}
