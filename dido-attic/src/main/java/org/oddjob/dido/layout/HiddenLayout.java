package org.oddjob.dido.layout;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;

/**
 * 
 * @author rob
 *
 */
public class HiddenLayout extends LayoutValueNode<Object> {

	private static final Logger logger = Logger.getLogger(HiddenLayout.class);
	
	@Override
	public Class<?> getType() {
		return Object.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
				
		return nextReaderFor(dataIn);
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		return nextWriterFor(dataOut);
	}
	
	public void setValue(Object value) {
		value(value);
		if (logger.isTraceEnabled()) {
			logger.trace("[" + this + "] set value [" + value + "]");
		}
	}
	
	public Object getValue() {
		return value();
	}
}
