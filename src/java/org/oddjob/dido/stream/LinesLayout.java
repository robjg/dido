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
		}
	}
 
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		final LinesIn linesIn = dataIn.provide(LinesIn.class);

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
				
				nextWriter = nextWriterFor(linesOut);
			}
			
			boolean keep = nextWriter.write(value);
			
			String text = value();
			
			if (text != null) {
				linesOut.writeLine(text);
				
				logger.trace("[" + LinesLayout.this + "] wrote line [" + 
						text + "]");
			}

			if (!keep) {
				nextWriter.close();
				nextWriter = null;
			}
			
			return true;
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextWriter != null) {
				
				logger.trace("Closing [" + nextWriter + "]");
				
				nextWriter.close();
				nextWriter = null;
			}
		}
	};

	
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {
		
		final LinesOut linesOut = dataOut.provide(LinesOut.class);
		
		return new LineWriter(linesOut);
	}
	
}
