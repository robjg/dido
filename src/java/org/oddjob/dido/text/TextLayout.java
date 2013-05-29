package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutValueNode;


public class TextLayout 
extends LayoutValueNode<String> {

	private int from;

	private int length;
	
	private boolean raw;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn)
			throws DataException {
		
		TextIn textIn = dataIn.provide(TextIn.class);
		
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
				
		final StringTextIn substr = new StringTextIn(newText);
		
		return new DataReader() {
			
			DataReader nextReader;
			
			@Override
			public Object read() throws DataException {
				
				if (nextReader == null) {

					value(substr.toString());
					
					nextReader = nextReaderFor(substr);
				}
				
				return nextReader.read();
			}
		};
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut)
	throws DataException {
		
		final TextOut outgoing = dataOut.provide(TextOut.class);
		
		final StringTextOut nextOut = new StringTextOut();
		
		return new DataWriter() {
			
			DataWriter nextWriter;
			
			@Override
			public boolean write(Object object) throws DataException {
				
				if (nextWriter == null) {
					
					value(null);
					nextWriter = nextWriterFor(nextOut);
				}
				
				if (nextWriter.write(object)) {
					return true;
				}
					
				String value = value();
				
				if (value != null) {
					outgoing.write(value, from, length == 0 ? value.length() : length);
				}
				
				nextWriter = null;
					
				return false;
			}
		};
		
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
