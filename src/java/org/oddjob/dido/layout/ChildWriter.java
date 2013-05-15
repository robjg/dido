package org.oddjob.dido.layout;

import java.util.Iterator;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOutProvider;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.DataWriterFactory;

/**
 * 
 * @author rob
 *
 */
public class ChildWriter implements DataWriter {

	private final Iterator<? extends DataWriterFactory> iterator;
	
	private final DataOutProvider dataOut;
	
	private DataWriter current;
	
	public ChildWriter(Iterable<? extends DataWriterFactory> children,
			DataOutProvider dataOut) {
		iterator = children.iterator();
		this.dataOut = dataOut;
	}	
	
	@Override
	public boolean write(Object value) throws DataException {
		
		if (current == null && iterator.hasNext()) {
			current = iterator.next().writerFor(dataOut);
		}
		
		if (current == null) {
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
}
