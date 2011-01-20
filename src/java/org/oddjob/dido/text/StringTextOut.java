package org.oddjob.dido.text;

import org.oddjob.dido.DataException;


abstract public class StringTextOut implements TextOut {

	public static final char PAD_CHARACTER = ' ';
	
	private StringBuilder buffer;

	public StringTextOut() {
		buffer = new StringBuilder();
	}
	
	@Override
	public void append(String text) {
		buffer.append(text);
	}
	
	@Override
	public void write(String text, int from, int length) {
		while (buffer.length() < from) {
			buffer.append(PAD_CHARACTER);
		}
		StringBuilder minibuf;
		if (length < 0 || length > text.length()) {
			minibuf = new StringBuilder(text);
		} else {
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
	}
	
	protected void clear() {
		buffer = new StringBuilder();
	}
	
	@Override
	abstract public boolean flush() throws DataException;
	
	@Override
	public String toString() {
		return buffer.toString();
	}
}
