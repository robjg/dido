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
	
	private DataReader current;
	
	public ChildReader(Iterable<? extends DataReaderFactory> children,
			DataIn dataIn) {
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
