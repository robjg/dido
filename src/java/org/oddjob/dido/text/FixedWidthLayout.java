package org.oddjob.dido.text;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutValueNode;
import org.oddjob.dido.other.When;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesOut;

/**
 * @oddjob.description Define a fixed width layout.
 * <p>
 * <ul>
 * <li>This is generally a top level layout.</li> 
 * <li>It can be nested in a {@link When} layout.</li>
 * <li>It can be a child layout of a {link {@link TextLayout2} layout to
 * further break up a region of text.</li>
 * </ul>
 * <p>
 * 
 * @author rob
 *
 */
public class FixedWidthLayout extends LayoutValueNode<String> {

	private static final Logger logger = Logger.getLogger(FixedWidthLayout.class);
	
	public static final String DEFAULT = ",";
	
	private TextFieldsIn fieldsIn;
	
	private TextFieldsOut fieldsOut;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}

	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	
	class MainReader implements DataReader {
		
		private final LinesIn linesIn;
		
		private DataReader nextReader;
		
		public MainReader(LinesIn linesIn) {

			this.linesIn = linesIn;			
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader != null) {
				Object value = nextReader.read();
				
				if (value != null) {
					return value;
				}
				else {
					nextReader.close();
				}
			}
			
			String line = linesIn.readLine();
			
			logger.trace("[" + FixedWidthLayout.this + "] read line [" + 
					line + "]");
			
			if (line == null) {
				return null;
			}
						
			value(line);

			fieldsIn.setText(line);
			
			nextReader = nextReaderFor(fieldsIn);
			
			return read();
		}
		
		@Override
		public void close() throws DataException {
			linesIn.close();
		}
	}
	

	@Override
	public DataReader readerFor(DataIn dataIn)
	throws DataException {
		
		LinesIn linesIn = dataIn.provideDataIn(LinesIn.class);

		if (fieldsIn == null) {
			fieldsIn = new TextFieldsIn();
		}
		
		return new MainReader(linesIn);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		fieldsIn = null;
		fieldsOut = null;
	}
		
	class MainWriter implements DataWriter {
		
		private final LinesOut linesOut;
		
		private DataWriter nextWriter;
		
		public MainWriter(LinesOut linesOut) {
			this.linesOut = linesOut;
		}
		
		@Override
		public boolean write(Object object) throws DataException {
			
			if (nextWriter == null) {

				fieldsOut.clear();
				nextWriter = nextWriterFor(fieldsOut);
			}
			
			if (nextWriter.write(object)) {
				return true;
			}
					
			if (fieldsOut.isWrittenTo()) {
				value(fieldsOut.getText());
			}
			
			if (isWrittenTo()) {
					
				resetWrittenTo();
				fieldsOut.resetWrittenTo();
				
				return write(object);
			}
			else {
				
				String value = value();
				
				if (value != null) {

					linesOut.writeLine(value);
					
					logger.trace("[" + FixedWidthLayout.this + "] wrote line [" + 
							value + "]");
				}
				
				nextWriter.close();
				nextWriter = null;
				
				return linesOut.isMultiLine();
			}
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
		
		@Override
		public String toString() {
			
			return "Writer for [" + FixedWidthLayout.this + "]";
		}
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {

		LinesOut linesOut = dataOut.provideDataOut(LinesOut.class);

		logger.trace("Creating writer for [" + linesOut + "]");

		if (fieldsOut == null) {
			fieldsOut = new TextFieldsOut();
		}
		
		return new MainWriter(linesOut);
	}
	
}
