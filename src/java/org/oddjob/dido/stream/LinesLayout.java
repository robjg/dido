package org.oddjob.dido.stream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextOut;

public class LinesLayout extends LayoutValueNode<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws UnsupportedeDataInException {
		
		final LinesIn linesIn = dataIn.provideIn(LinesIn.class);

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
					
					StringTextIn textIn = new StringTextIn(line);
							
					nextReader = nextReaderFor(textIn);
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
		
		final LinesOut linesOut = dataOut.provideOut(LinesOut.class);
		
		return new DataWriter() {

			DataWriter nextWriter;
			
			TextOut textOut;
			
			@Override
			public boolean write(Object value) throws DataException {
				
				if (nextWriter == null) {
					
					textOut = new StringTextOut();
					
					nextWriter = nextWriterFor(textOut);
							
				}
				
				if (nextWriter.write(value)) {
					return true;
				}
				
				if (textOut.hasData()) {
					linesOut.writeLine(textOut.toValue(String.class));
				}

				nextWriter = null;
				
				return false;
			}
		};
	}
	
	@Override
	public void close() {
		value(null);
	}
}
