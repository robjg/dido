package org.oddjob.dido.stream;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutValueNode;

/**
 * @oddjob.description Represent lines in the layout of a file. 
 * 
 * @author rob
 *
 */
public class LinesLayout extends LayoutValueNode<String> {

	private static final Logger logger = Logger.getLogger(LinesLayout.class);

	private LinesOut linesOut;
	
	private LinesIn linesIn;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	private class LineReader implements DataReader {
		
		private DataReader nextReader;
		
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
		
		this.linesIn = dataIn.provideDataIn(LinesIn.class);

		return new LineReader();			
	}
	
	private class LineWriter implements DataWriter {

		private DataWriter nextWriter;
		
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
		
		this.linesOut = dataOut.provideDataOut(LinesOut.class);
		
		return new LineWriter();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		linesIn = null;
		linesOut = null;
	}
	
	public int getLineCount() {
		if (linesIn != null) {
			return linesIn.getLinesRead();
		}
		else if (linesOut != null) {
			return linesOut.getLinesWritten();
		}
		else {
			return 0;
		}
	}
}
