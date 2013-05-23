package org.oddjob.dido.stream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.layout.LayoutValueNode;

public class LinesLayout extends LayoutValueNode<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		final LinesIn linesIn = dataIn.provide(LinesIn.class);

		return new DataReader() {
			
			DataReader nextReader;
			
			@Override
			public Object read() throws DataException {
				
				if (nextReader == null) {

					String line = linesIn.readLine();
					
					if (line == null) {
						return null;
					}
					
					value(line);
					
					nextReader = nextReaderFor(linesIn);
				}							
				
				Object next = nextReader.read();
				
				if (next == null) {
					
					nextReader = null;
					
					return read();
				}
				else {
					return next;
				}
			}
		};
			
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
			throws UnsupportedeDataOutException {
		
		final LinesOut linesOut = dataOut.provide(LinesOut.class);
		
		return new DataWriter() {

			DataWriter nextWriter;
			
			@Override
			public boolean write(Object value) throws DataException {
				
				if (nextWriter == null) {
					
					value(null);
					
					nextWriter = nextWriterFor(linesOut);
							
				}
				
				if (nextWriter.write(value)) {
					return true;
				}
				
				String text = value();
				
				if (text != null) {
					linesOut.writeLine(text);
				}

				nextWriter = null;
				
				return false;
			}
		};
	}
	
	@Override
	public void reset() {
		value(null);
	}
}
