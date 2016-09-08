package org.oddjob.dido.other;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutNode;


public class When 
extends LayoutNode
implements CaseCondition<String> {

	private String value;
	
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

	@Override
	public void reset() {
	}
		
	@Override
	public boolean evaluate(String against) {
		return value.equals(against);
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	public String value() {
		return value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
