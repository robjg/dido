package org.oddjob.dido.text;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutValueNode;


/**
 * @oddjob.description A field. 
 * 
 * @author rob
 *
 */
public class TextLayout 
extends LayoutValueNode<String> {

	private static final Logger logger = Logger.getLogger(TextLayout.class);
	
	private boolean raw;
	
	private int from;

	private int length;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	class TextReader implements DataReader {
		
		final StringTextIn substr;
				
		DataReader nextReader;
		
		public TextReader(StringTextIn substr) {
			this.substr = substr;
		}
		
		@Override
		public Object read() throws DataException {
			
			if (nextReader == null) {

				value(substr.getText());
				
				nextReader = nextReaderFor(substr);
			}
			
			return nextReader.read();
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextReader != null) {
				nextReader.close();
			}
		}
		
		@Override
		public String toString() {
			
			return DataReader.class.getSimpleName() + " for [" + 
					TextLayout.this + "]";
		}
	};
	
	@Override
	public DataReader readerFor(DataIn dataIn)
			throws DataException {
		
		TextIn textIn = dataIn.provideDataIn(TextIn.class);
		
		String inString = textIn.getText();
		
		String newText;

		if (from > inString.length()) {
			newText = "";
		}
		else if (length > 0) {
			int to = from + length;
			if (to > inString.length()) {
				to = inString.length();
			}
			newText = inString.substring(from, to);			
		}
		else {
			newText = inString.substring(from);
		}
		if (!raw) {
			newText = newText.trim();
		}
				
		return new TextReader(new StringTextIn(newText));		
	}
	
	class TextWriter implements DataWriter {
		
		private final TextOut outgoing;
		
		private final DataWriter nextWriter;
		
		private final StringTextOut textOut;
		
		public TextWriter(TextOut outgoing) throws DataException {
			
			this.outgoing = outgoing;
			
			this.textOut = new StringTextOut();
			
			this.nextWriter = nextWriterFor(textOut);
			
			value(null);
		}
		
		@Override
		public boolean write(Object object) throws DataException {
			
			if (nextWriter.write(object)) {
				return true;
			}
				
			if (textOut.isWrittenTo()) {
				value(textOut.toText());
			}
			
			if (isWrittenTo()) {
				
				resetWrittenTo();
				textOut.resetWrittenTo();
				
				return write(object);
			}
			
			String value = value();
			
			if (value != null) {
				
				int theLength = length == 0 ? value.length() : length;
				
				outgoing.write(value, from, theLength);
				
				logger.trace("[" + TextLayout.this + "] wrote [" + 
						value + "] to [" + outgoing + "]");
			}
			else {
				logger.trace("[" + TextLayout.this + "] no value.");
				
			}
						
			return false;
		}
		
		@Override
		public void close() throws DataException {
			
			logger.trace("Closing [" + nextWriter + "]");
			
			nextWriter.close();
		}
		
		@Override
		public String toString() {
			
			return DataWriter.class.getSimpleName() + " for [" + 
					TextLayout.this + "]";
		}
	};
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {
		
		final TextOut outgoing = dataOut.provideDataOut(TextOut.class);
		
		logger.trace("Creating writer for [" + outgoing + "]");
		
		return new TextWriter(outgoing);
	}

	@Override
	public void reset() {
	}
	
	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isRaw() {
		return raw;
	}

	public void setRaw(boolean trim) {
		this.raw = trim;
	}
	
	public String getValue() {
		return this.value();
	}
}
