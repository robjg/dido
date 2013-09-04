package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.stream.LinesOut;


public class StringTextOut implements TextOut, LinesOut {

	public static final char PAD_CHARACTER = ' ';
	
	private StringBuilder buffer;

	private boolean writtenTo;
	
	@Override
	public void append(String text) {
		if (buffer == null) {
			buffer = new StringBuilder();
		}
		buffer.append(text);
		writtenTo = true;
	}
	
	@Override
	public void writeLine(String text) throws DataException {
		append(text);
	}

	@Override
	public String lastLine() {
		return toText();
	}
	
	@Override
	public void write(String text, int from, int length) {
		if (buffer == null) {
			buffer = new StringBuilder();
		}
		
		while (buffer.length() < from) {
			buffer.append(PAD_CHARACTER);
		}
		
		StringBuilder minibuf;
		
		if (length < 0 || length > text.length()) {
			minibuf = new StringBuilder(text);
		} 
		else {
			minibuf = new StringBuilder(text.substring(0, length));			
		}
		
		while (minibuf.length() < length) {
			minibuf.append(PAD_CHARACTER);
		}
		
		if (from < buffer.length() ) {
			buffer.replace(from, from + minibuf.length(), minibuf.toString());
		}
		else {
			buffer.append(minibuf.toString());
		}
		
		writtenTo = true;
	}
	
	public void clear() {
		buffer = null;
		writtenTo = false;
	}
	
	public int length() {
		if (buffer == null) {
			return 0;
		}
		else {
			return buffer.length();
		}
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
	throws UnsupportedDataOutException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}

		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + (buffer == null 
				? " unwritten" :  " length " + buffer.length());
	}
	
	@Override
	public boolean isWrittenTo() {
		return writtenTo;
	}
	
	public void resetWrittenTo() {
		writtenTo = false;
	}
	
	@Override
	public boolean isMultiLine() {
		return false;
	}
	
	@Override
	public void close() throws DataException {
		// Nothing to close.
	}
	
	public String toText() {
	
		if (buffer == null) {
			return null;
		}
		else {
			return buffer.toString();
		}
	}
}
