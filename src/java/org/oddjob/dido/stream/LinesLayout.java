package org.oddjob.dido.stream;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutValueNode;

public class LinesLayout extends LayoutValueNode<String> {

	private static final Logger logger = Logger.getLogger(LinesLayout.class);
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	private class LineReader implements DataReader {
		
		private final LinesIn linesIn;
		
		private DataReader nextReader;
		
		public LineReader(LinesIn linesIn) {
			this.linesIn = linesIn;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader == null) {

				String line = linesIn.readLine();
				
				logger.trace("[" + LinesLayout.this + "] read line [" + 
						line + "]");
				
				if (line == null) {
					return null;
				}
				
				value(line);
				
				nextReader = nextReaderFor(linesIn);
			}							
			
			Object next = nextReader.read();
			
			if (next == null) {

				nextReader.close();
				nextReader = null;
				
				return read();
			}
			else {
				return next;
			}
		}
		
		@Override
		public void close() throws DataException {
			if (nextReader != null) {
				nextReader.close();
			}
			linesIn.close();
		}
	}
 
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		final LinesIn linesIn = dataIn.provideDataIn(LinesIn.class);

		return new LineReader(linesIn);			
	}
	
	private class LineWriter implements DataWriter {

		private final LinesOut linesOut;
		
		private DataWriter nextWriter;
		
		public LineWriter(LinesOut linesOut) {
			this.linesOut = linesOut;
		}
		
		@Override
		public boolean write(Object value) throws DataException {
			
			if (nextWriter == null) {
				
				value(null);
				linesOut.resetWrittenTo();
				
				nextWriter = nextWriterFor(linesOut);
			}
			
			if (nextWriter.write(value)) {
				return true;
			}
			
			if (linesOut.isWrittenTo()) {
				value(linesOut.lastLine());
			}
			
			if (isWrittenTo()) {
				
				String text = value();
				
				linesOut.writeLine(text);
				
				logger.trace("[" + LinesLayout.this + "] wrote line [" + 
						text + "]");
				
				resetWrittenTo();
				linesOut.resetWrittenTo();
				
				return write(value);
			}
			else {
				logger.debug("[" + LinesLayout.this + "] Nothing to write.");
			}

			nextWriter.close();
			nextWriter = null;
			
			return true;
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextWriter != null) {
				
				logger.trace("Closing [" + nextWriter + "]");
				
				nextWriter.close();
				nextWriter = null;
			}
			
			linesOut.close();
		}
	};
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {
		
		final LinesOut linesOut = dataOut.provideDataOut(LinesOut.class);
		
		return new LineWriter(linesOut);
	}
	
}
